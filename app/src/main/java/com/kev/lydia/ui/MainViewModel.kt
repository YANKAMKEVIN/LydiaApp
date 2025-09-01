package com.kev.lydia.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kev.data.network.NetworkObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MainViewModel @Inject constructor(
    private val networkObserver: NetworkObserver
) : ViewModel() {

    val isOffline: StateFlow<Boolean> = networkObserver.isConnected
        .map { !it }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)
}
