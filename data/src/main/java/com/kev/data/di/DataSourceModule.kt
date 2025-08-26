package com.kev.data.di

import android.content.Context
import androidx.room.Room
import com.kev.data.api.ContactApi
import com.kev.data.datasource.local.ContactDatabase
import com.kev.data.datasource.remote.ContactDataSource
import com.kev.data.datasource.remote.impl.ContactDataSourceImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {

    @Provides
    @Singleton
    fun provideContactDataSource(contactApi: ContactApi): ContactDataSource {
        return ContactDataSourceImpl(contactApi)
    }

    @Provides
    @Singleton
    fun provideContactDataBase(@ApplicationContext context: Context): ContactDatabase {
        return Room.databaseBuilder(
            context,
            ContactDatabase::class.java,
            "contacts.db"
        ).fallbackToDestructiveMigration().build()
    }

}