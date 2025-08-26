package com.kev.data.di

import com.kev.data.api.ContactApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideContactApi(): ContactApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL_REAL_ESTATE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ContactApi::class.java)
    }

    companion object {
        private const val BASE_URL_REAL_ESTATE = "https://randomuser.me/"
    }
}