package com.kev.lydia.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kev.data.network.NetworkObserver
import com.kev.domain.model.Contact
import com.kev.domain.usecase.GetContactsListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getContactsListUseCase: GetContactsListUseCase,
    private val networkObserver: NetworkObserver,
) : ViewModel() {

    private val _contacts = MutableStateFlow(PagingData.empty<Contact>())
    val contacts: StateFlow<PagingData<Contact>> = _contacts
    val isOffline: StateFlow<Boolean> = networkObserver.isConnected
        .map { !it }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    init {
        viewModelScope.launch {
            getContactsListUseCase().cachedIn(viewModelScope)
                .collect { pagingData ->
                    _contacts.value = pagingData
                }
        }
    }
}
