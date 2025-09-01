package com.kev.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ContactEntity::class, RemoteKeys::class], version = 3)

abstract class ContactDatabase : RoomDatabase() {
    abstract val remoteKeysDao: RemoteKeysDao
    abstract val contactDao: ContactDao
}