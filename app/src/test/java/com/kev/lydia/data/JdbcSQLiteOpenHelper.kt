package com.kev.lydia.data

import android.content.ContentResolver
import android.content.ContentValues
import android.database.CharArrayBuffer
import android.database.ContentObserver
import android.database.Cursor
import android.database.DataSetObserver
import android.database.sqlite.SQLiteTransactionListener
import android.net.Uri
import android.os.Bundle
import android.os.CancellationSignal
import android.util.Pair
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.SupportSQLiteProgram
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.sqlite.db.SupportSQLiteStatement
import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.util.Locale
import java.util.concurrent.locks.ReentrantLock

/**
 * A [SupportSQLiteOpenHelper.Factory] backed by a real SQLite through JDBC (sqlite-jdbc), so Room
 * databases, DAOs and migrations run as plain JVM unit tests (the Robolectric tests of :data
 * cannot run everywhere).
 *
 * Only what Room uses is implemented. A database without a name lives in memory; a named one is a
 * file in [directory], so it can be prepared at an older version before Room opens it.
 */
class JdbcSQLiteOpenHelperFactory(private val directory: File? = null) : SupportSQLiteOpenHelper.Factory {
    override fun create(configuration: SupportSQLiteOpenHelper.Configuration): SupportSQLiteOpenHelper =
        JdbcSQLiteOpenHelper(configuration.name?.let { File(requireNotNull(directory), it) }, configuration.callback)
}

/** Mimics android.database.sqlite.SQLiteOpenHelper: creates or upgrades the database when first opened. */
private class JdbcSQLiteOpenHelper(
    private val file: File?,
    private val callback: SupportSQLiteOpenHelper.Callback,
) : SupportSQLiteOpenHelper {

    private var database: JdbcSQLiteDatabase? = null

    override val databaseName: String? get() = file?.name

    override fun setWriteAheadLoggingEnabled(enabled: Boolean) = Unit

    override val writableDatabase: SupportSQLiteDatabase get() = open()
    override val readableDatabase: SupportSQLiteDatabase get() = open()

    @Synchronized
    private fun open(): SupportSQLiteDatabase {
        database?.let { if (it.isOpen) return it }
        val db = JdbcSQLiteDatabase(file)
        callback.onConfigure(db)
        val version = db.version
        if (version != callback.version) {
            db.beginTransaction()
            try {
                when {
                    version == 0 -> callback.onCreate(db)
                    version < callback.version -> callback.onUpgrade(db, version, callback.version)
                    else -> callback.onDowngrade(db, version, callback.version)
                }
                db.version = callback.version
                db.setTransactionSuccessful()
            } finally {
                db.endTransaction()
            }
        }
        callback.onOpen(db)
        database = db
        return db
    }

    @Synchronized
    override fun close() {
        database?.close()
        database = null
    }
}

/** Opens [file] (or an in-memory database) with sqlite-jdbc. */
fun openJdbcConnection(file: File?): Connection =
    DriverManager.getConnection(if (file == null) "jdbc:sqlite::memory:" else "jdbc:sqlite:${file.absolutePath}")

/**
 * One JDBC connection shared by Room's threads. Like Android's SQLiteDatabase, a transaction
 * belongs to the thread that began it: the lock is held from the first beginTransaction to the
 * last endTransaction, and every other statement waits for it.
 */
private class JdbcSQLiteDatabase(private val file: File?) : SupportSQLiteDatabase {

    private val connection: Connection = openJdbcConnection(file)
    private val lock = ReentrantLock()
    private var transactionDepth = 0
    private var transactionFailed = false
    private var currentLevelSuccessful = false
    private val successStack = ArrayDeque<Boolean>()

    fun <T> locked(block: () -> T): T {
        lock.lock()
        try {
            return block()
        } finally {
            lock.unlock()
        }
    }

    fun prepare(sql: String, args: List<Any?>): PreparedStatement =
        connection.prepareStatement(sql).also { statement ->
            args.forEachIndexed { index, value -> statement.setObject(index + 1, value) }
        }

    // region Transactions
    override fun beginTransaction() {
        lock.lock()
        if (transactionDepth == 0) {
            connection.createStatement().use { it.execute("BEGIN IMMEDIATE") }
            transactionFailed = false
        } else {
            successStack.addLast(currentLevelSuccessful)
        }
        currentLevelSuccessful = false
        transactionDepth++
    }

    override fun beginTransactionNonExclusive() = beginTransaction()
    override fun beginTransactionWithListener(transactionListener: SQLiteTransactionListener) = beginTransaction()
    override fun beginTransactionWithListenerNonExclusive(transactionListener: SQLiteTransactionListener) =
        beginTransaction()

    override fun setTransactionSuccessful() {
        check(lock.isHeldByCurrentThread && transactionDepth > 0) { "No transaction on this thread" }
        currentLevelSuccessful = true
    }

    override fun endTransaction() {
        check(lock.isHeldByCurrentThread && transactionDepth > 0) { "No transaction on this thread" }
        try {
            if (!currentLevelSuccessful) transactionFailed = true
            transactionDepth--
            if (transactionDepth == 0) {
                connection.createStatement().use { it.execute(if (transactionFailed) "ROLLBACK" else "COMMIT") }
            } else {
                currentLevelSuccessful = successStack.removeLast()
            }
        } finally {
            lock.unlock()
        }
    }

    override fun inTransaction(): Boolean = lock.isHeldByCurrentThread && transactionDepth > 0
    override val isDbLockedByCurrentThread: Boolean get() = lock.isHeldByCurrentThread
    override fun yieldIfContendedSafely(): Boolean = false
    override fun yieldIfContendedSafely(sleepAfterYieldDelayMillis: Long): Boolean = false
    // endregion

    // region Statements and queries
    override fun compileStatement(sql: String): SupportSQLiteStatement = JdbcStatement(this, sql)

    override fun query(query: String): Cursor = query(SimpleSQLiteQuery(query))
    override fun query(query: String, bindArgs: Array<out Any?>): Cursor =
        query(SimpleSQLiteQuery(query, bindArgs))

    override fun query(query: SupportSQLiteQuery, cancellationSignal: CancellationSignal?): Cursor = query(query)

    override fun query(query: SupportSQLiteQuery): Cursor {
        val bindings = Bindings()
        query.bindTo(bindings)
        return locked {
            prepare(query.sql, bindings.values()).use { statement ->
                if (!statement.execute()) return@locked ListCursor(emptyList(), emptyList())
                statement.resultSet.use { resultSet ->
                    val meta = resultSet.metaData
                    val columns = (1..meta.columnCount).map { meta.getColumnLabel(it) }
                    val rows = buildList {
                        while (resultSet.next()) add(Array(columns.size) { resultSet.getObject(it + 1) })
                    }
                    ListCursor(columns, rows)
                }
            }
        }
    }

    override fun execSQL(sql: String) = execSQL(sql, emptyArray())

    override fun execSQL(sql: String, bindArgs: Array<out Any?>) {
        locked { prepare(sql, bindArgs.toList()).use { it.execute() } }
    }

    override fun insert(table: String, conflictAlgorithm: Int, values: ContentValues): Long =
        throw UnsupportedOperationException("Room inserts through compiled statements")

    override fun delete(table: String, whereClause: String?, whereArgs: Array<out Any?>?): Int =
        throw UnsupportedOperationException("Room deletes through compiled statements")

    override fun update(
        table: String,
        conflictAlgorithm: Int,
        values: ContentValues,
        whereClause: String?,
        whereArgs: Array<out Any?>?
    ): Int = throw UnsupportedOperationException("Room updates through compiled statements")
    // endregion

    // region Database properties
    override var version: Int
        get() = locked { connection.createStatement().use { s -> s.executeQuery("PRAGMA user_version").use { it.getInt(1) } } }
        set(value) = locked { connection.createStatement().use { it.execute("PRAGMA user_version = $value") } }

    override val maximumSize: Long get() = Long.MAX_VALUE
    override fun setMaximumSize(numBytes: Long): Long = Long.MAX_VALUE
    override var pageSize: Long
        get() = 4096
        set(_) = Unit

    override val isReadOnly: Boolean get() = false
    override val isOpen: Boolean get() = !connection.isClosed
    override fun needUpgrade(newVersion: Int): Boolean = newVersion > version
    override val path: String? get() = file?.absolutePath
    override fun setLocale(locale: Locale) = Unit
    override fun setMaxSqlCacheSize(cacheSize: Int) = Unit
    override fun setForeignKeyConstraintsEnabled(enabled: Boolean) =
        execSQL("PRAGMA foreign_keys = ${if (enabled) "ON" else "OFF"}")

    override fun enableWriteAheadLogging(): Boolean = false
    override fun disableWriteAheadLogging() = Unit
    override val isWriteAheadLoggingEnabled: Boolean get() = false
    override val attachedDbs: List<Pair<String, String>>? get() = null
    override val isDatabaseIntegrityOk: Boolean get() = true

    override fun close() = locked { connection.close() }
    // endregion
}

/** Collects the arguments Room binds to a statement or a query (1-based indexes). */
private open class Bindings : SupportSQLiteProgram {
    private val args = sortedMapOf<Int, Any?>()

    fun values(): List<Any?> = (1..(args.keys.maxOrNull() ?: 0)).map { args[it] }

    override fun bindNull(index: Int) { args[index] = null }
    override fun bindLong(index: Int, value: Long) { args[index] = value }
    override fun bindDouble(index: Int, value: Double) { args[index] = value }
    override fun bindString(index: Int, value: String) { args[index] = value }
    override fun bindBlob(index: Int, value: ByteArray) { args[index] = value }
    override fun clearBindings() = args.clear()
    override fun close() = Unit
}

private class JdbcStatement(
    private val database: JdbcSQLiteDatabase,
    private val sql: String,
) : Bindings(), SupportSQLiteStatement {

    override fun execute() {
        database.locked { database.prepare(sql, values()).use { it.execute() } }
    }

    override fun executeUpdateDelete(): Int =
        database.locked { database.prepare(sql, values()).use { it.executeUpdate() } }

    /** Like Android: the new row id, or -1 when no row was inserted (e.g. INSERT OR IGNORE). */
    override fun executeInsert(): Long = database.locked {
        val changed = database.prepare(sql, values()).use { it.executeUpdate() }
        if (changed == 0) -1L else database.prepare("SELECT last_insert_rowid()", emptyList()).use { s ->
            s.executeQuery().use { it.getLong(1) }
        }
    }

    override fun simpleQueryForLong(): Long =
        database.locked { database.prepare(sql, values()).use { s -> s.executeQuery().use { it.getLong(1) } } }

    override fun simpleQueryForString(): String? =
        database.locked { database.prepare(sql, values()).use { s -> s.executeQuery().use { it.getString(1) } } }
}

/** A read-only [Cursor] over rows already read from JDBC. */
private class ListCursor(
    private val columns: List<String>,
    private val rows: List<Array<Any?>>,
) : Cursor {
    private var position = -1
    private var closed = false

    private fun value(column: Int): Any? = rows[position][column]

    override fun getCount(): Int = rows.size
    override fun getPosition(): Int = position
    override fun move(offset: Int): Boolean = moveToPosition(position + offset)
    override fun moveToPosition(position: Int): Boolean {
        this.position = position.coerceIn(-1, rows.size)
        return position in rows.indices
    }

    override fun moveToFirst(): Boolean = moveToPosition(0)
    override fun moveToLast(): Boolean = moveToPosition(rows.size - 1)
    override fun moveToNext(): Boolean = moveToPosition(position + 1)
    override fun moveToPrevious(): Boolean = moveToPosition(position - 1)
    override fun isFirst(): Boolean = rows.isNotEmpty() && position == 0
    override fun isLast(): Boolean = rows.isNotEmpty() && position == rows.size - 1
    override fun isBeforeFirst(): Boolean = rows.isEmpty() || position == -1
    override fun isAfterLast(): Boolean = rows.isEmpty() || position == rows.size

    override fun getColumnIndex(columnName: String): Int =
        columns.indexOfFirst { it.equals(columnName, ignoreCase = true) }

    override fun getColumnIndexOrThrow(columnName: String): Int =
        getColumnIndex(columnName).also { require(it >= 0) { "column '$columnName' does not exist" } }

    override fun getColumnName(columnIndex: Int): String = columns[columnIndex]
    override fun getColumnNames(): Array<String> = columns.toTypedArray()
    override fun getColumnCount(): Int = columns.size

    override fun getBlob(columnIndex: Int): ByteArray? = value(columnIndex) as ByteArray?
    override fun getString(columnIndex: Int): String? = value(columnIndex)?.toString()
    override fun getShort(columnIndex: Int): Short = getLong(columnIndex).toShort()
    override fun getInt(columnIndex: Int): Int = getLong(columnIndex).toInt()
    override fun getLong(columnIndex: Int): Long = when (val v = value(columnIndex)) {
        null -> 0L
        is Number -> v.toLong()
        else -> v.toString().toLongOrNull() ?: 0L
    }

    override fun getFloat(columnIndex: Int): Float = getDouble(columnIndex).toFloat()
    override fun getDouble(columnIndex: Int): Double = when (val v = value(columnIndex)) {
        null -> 0.0
        is Number -> v.toDouble()
        else -> v.toString().toDoubleOrNull() ?: 0.0
    }

    override fun getType(columnIndex: Int): Int = when (value(columnIndex)) {
        null -> Cursor.FIELD_TYPE_NULL
        is Float, is Double -> Cursor.FIELD_TYPE_FLOAT
        is Number -> Cursor.FIELD_TYPE_INTEGER
        is ByteArray -> Cursor.FIELD_TYPE_BLOB
        else -> Cursor.FIELD_TYPE_STRING
    }

    override fun isNull(columnIndex: Int): Boolean = value(columnIndex) == null

    override fun copyStringToBuffer(columnIndex: Int, buffer: CharArrayBuffer?) =
        throw UnsupportedOperationException()

    @Deprecated("Deprecated in Java")
    override fun deactivate() = Unit

    @Deprecated("Deprecated in Java")
    override fun requery(): Boolean = false
    override fun close() { closed = true }
    override fun isClosed(): Boolean = closed
    override fun registerContentObserver(observer: ContentObserver?) = Unit
    override fun unregisterContentObserver(observer: ContentObserver?) = Unit
    override fun registerDataSetObserver(observer: DataSetObserver?) = Unit
    override fun unregisterDataSetObserver(observer: DataSetObserver?) = Unit
    override fun setNotificationUri(cr: ContentResolver?, uri: Uri?) = Unit
    override fun getNotificationUri(): Uri? = null
    override fun getWantsAllOnMoveCalls(): Boolean = false
    override fun setExtras(extras: Bundle?) = Unit
    override fun getExtras(): Bundle? = null
    override fun respond(extras: Bundle?): Bundle? = null
}
