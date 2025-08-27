package com.kev.domain.usecase

import androidx.paging.PagingData
import com.kev.domain.model.Contact
import kotlinx.coroutines.flow.Flow

fun interface GetContactsListUseCase {
    suspend operator fun invoke(): Flow<PagingData<Contact>>
}