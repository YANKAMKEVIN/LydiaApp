package com.kev.data.datasource.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT contactId FROM favorites")
    fun observeIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun add(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE contactId = :contactId")
    suspend fun remove(contactId: String)

    /** Cached contacts that are favourites, in the order of the contact list. */
    @Query(
        "SELECT contactentity.* FROM contactentity " +
            "INNER JOIN favorites ON favorites.contactId = contactentity.id " +
            "ORDER BY contactentity.rowid"
    )
    fun favoriteContactsPagingSource(): PagingSource<Int, ContactEntity>
}
