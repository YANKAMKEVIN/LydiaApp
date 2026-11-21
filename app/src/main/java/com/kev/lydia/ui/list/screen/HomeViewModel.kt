package com.kev.lydia.ui.list.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kev.domain.model.Contact
import com.kev.domain.usecase.GetContactsListUseCase
import com.kev.domain.usecase.GetFavoriteContactsUseCase
import com.kev.domain.usecase.ObserveFavoriteIdsUseCase
import com.kev.domain.usecase.SetFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getContactsListUseCase: GetContactsListUseCase,
    private val getFavoriteContactsUseCase: GetFavoriteContactsUseCase,
    observeFavoriteIdsUseCase: ObserveFavoriteIdsUseCase,
    private val setFavoriteUseCase: SetFavoriteUseCase,
) : ViewModel() {

    private val _contacts = MutableStateFlow(PagingData.Companion.empty<Contact>())
    val contacts: StateFlow<PagingData<Contact>> = _contacts

    private val _favoriteContacts = MutableStateFlow(PagingData.empty<Contact>())

    /** Favourite contacts only, read from the cache (shown when [showFavoritesOnly] is on). */
    val favoriteContacts: StateFlow<PagingData<Contact>> = _favoriteContacts

    /** Ids of the favourite contacts, for the star of the contact detail. */
    val favoriteIds: StateFlow<Set<String>> = observeFavoriteIdsUseCase()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    private val _showFavoritesOnly = MutableStateFlow(false)

    /** Whether the list shows the "Favorites" filter instead of all the contacts. */
    val showFavoritesOnly: StateFlow<Boolean> = _showFavoritesOnly.asStateFlow()

    /**
     * Contacts shown by the detail pager, as they were listed (and filtered) when one was tapped.
     *
     * Kept in memory only: putting the whole list in a SavedStateHandle wrote it into the activity's
     * saved state, which grows with every loaded page and can exceed the Binder limit
     * (TransactionTooLargeException) when the app goes to the background on the detail screen.
     */
    var openedContacts: List<Contact> = emptyList()
        private set

    init {
        viewModelScope.launch {
            getContactsListUseCase().cachedIn(viewModelScope)
                .collect { pagingData ->
                    _contacts.value = pagingData
                }
        }
        viewModelScope.launch {
            getFavoriteContactsUseCase().cachedIn(viewModelScope)
                .collect { pagingData ->
                    _favoriteContacts.value = pagingData
                }
        }
    }

    /** Remembers [contacts] for the detail pager and returns the page to open for [clicked]. */
    fun openContact(clicked: Contact, contacts: List<Contact>): Int {
        openedContacts = contacts.toList()
        return openedContacts.indexOf(clicked).coerceAtLeast(0)
    }

    /** Switches the list between all the contacts and the favourites only. */
    fun toggleFavoritesOnly() {
        _showFavoritesOnly.value = !_showFavoritesOnly.value
    }

    /** Adds [contact] to the favourites, or removes it if it already is one. */
    fun toggleFavorite(contact: Contact) {
        val favorite = contact.id !in favoriteIds.value
        viewModelScope.launch { setFavoriteUseCase(contact.id, favorite) }
    }
}
