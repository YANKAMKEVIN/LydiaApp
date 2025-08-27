package com.kev.domain

import com.kev.domain.repository.ContactRepository
import com.kev.domain.usecase.GetContactsListUseCase
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
}