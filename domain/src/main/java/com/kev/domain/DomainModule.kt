package com.kev.domain

import com.kev.domain.repository.ContactRepository
import com.kev.domain.usecase.GetContactsListUseCase
import com.kev.domain.usecase.GetFavoriteContactsUseCase
import com.kev.domain.usecase.ObserveFavoriteIdsUseCase
import com.kev.domain.usecase.SetFavoriteUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DomainModule {
    @Singleton
    @Provides
    fun provideGetContactsListUseCase(contactRepository: ContactRepository): GetContactsListUseCase =
        GetContactsListUseCase(contactRepository::fetchContacts)

    @Singleton
    @Provides
    fun provideGetFavoriteContactsUseCase(contactRepository: ContactRepository): GetFavoriteContactsUseCase =
        GetFavoriteContactsUseCase(contactRepository::fetchFavoriteContacts)

    @Singleton
    @Provides
    fun provideObserveFavoriteIdsUseCase(contactRepository: ContactRepository): ObserveFavoriteIdsUseCase =
        ObserveFavoriteIdsUseCase(contactRepository::observeFavoriteIds)

    @Singleton
    @Provides
    fun provideSetFavoriteUseCase(contactRepository: ContactRepository): SetFavoriteUseCase =
        SetFavoriteUseCase(contactRepository::setFavorite)
}