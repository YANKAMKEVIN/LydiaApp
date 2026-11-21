package com.kev.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ContactEntity::class, RemoteKeys::class, FavoriteEntity::class],
    version = 4
)
abstract class ContactDatabase : RoomDatabase() {
    abstract val remoteKeysDao: RemoteKeysDao
    abstract val contactDao: ContactDao
    abstract val favoriteDao: FavoriteDao

    companion object {
        /** Version 4 adds the favourites table; the cached contacts are kept. */
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `favorites` " +
                        "(`contactId` TEXT NOT NULL, PRIMARY KEY(`contactId`))"
                )
            }
        }
    }
}
