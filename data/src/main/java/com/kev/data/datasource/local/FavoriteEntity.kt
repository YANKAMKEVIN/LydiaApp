package com.kev.data.datasource.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A contact marked as favourite.
 *
 * Kept in its own table rather than as a column of [ContactEntity]: the contacts table is cleared
 * on every refresh by the RemoteMediator, while favourites must survive it. Contact ids are
 * stable (the API is called with a fixed seed), so the refreshed contacts find their favourite
 * flag again.
 */
@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val contactId: String,
)
