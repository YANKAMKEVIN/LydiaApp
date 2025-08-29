package com.kev.lydia.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kev.domain.model.Contact
import com.kev.domain.usecase.GetContactsListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getContactsListUseCase: GetContactsListUseCase
) : ViewModel() {

    private val _contacts = MutableStateFlow(PagingData.empty<Contact>())
    val contacts: StateFlow<PagingData<Contact>> = _contacts

    init {
        viewModelScope.launch {
            getContactsListUseCase().cachedIn(viewModelScope)
                .collect { pagingData ->
                    _contacts.value = pagingData
                }
        }
    }
}
