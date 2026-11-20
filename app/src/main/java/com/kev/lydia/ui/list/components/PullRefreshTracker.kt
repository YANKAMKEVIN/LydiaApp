package com.kev.lydia.ui.list.components

/**
 * Drives the pull-to-refresh indicator: it shows from the moment the user pulls until the refresh
 * that pull started is over, and ignores the automatic refreshes Paging runs on its own (launch,
 * return of the network).
 */
data class PullRefreshTracker(
    val isRefreshing: Boolean = false,
    private val sawLoading: Boolean = false,
) {
    /** The user pulled; [refreshLoading] is the refresh state at that moment. */
    fun onPull(refreshLoading: Boolean): PullRefreshTracker =
        PullRefreshTracker(isRefreshing = true, sawLoading = refreshLoading)

    /** The refresh load state changed. */
    fun onRefreshLoading(refreshLoading: Boolean): PullRefreshTracker = when {
        !isRefreshing -> this
        refreshLoading -> copy(sawLoading = true)
        sawLoading -> PullRefreshTracker()
        else -> this
    }

    /** Safety net: stop the indicator if no refresh ever started. */
    fun onTimeout(): PullRefreshTracker = PullRefreshTracker()
}
