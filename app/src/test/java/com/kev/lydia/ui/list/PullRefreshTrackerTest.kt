package com.kev.lydia.ui.list

import com.kev.lydia.ui.list.components.PullRefreshTracker
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PullRefreshTrackerTest {

    @Test
    fun `automatic refreshes do not show the indicator`() {
        val tracker = PullRefreshTracker().onRefreshLoading(true)
        assertFalse(tracker.isRefreshing)
        assertFalse(tracker.onRefreshLoading(false).isRefreshing)
    }

    @Test
    fun `indicator shows from the pull until the refresh it started ends`() {
        var tracker = PullRefreshTracker().onPull(refreshLoading = false)
        assertTrue(tracker.isRefreshing)
        // refresh() has not switched the load state yet: keep spinning.
        tracker = tracker.onRefreshLoading(false)
        assertTrue(tracker.isRefreshing)
        tracker = tracker.onRefreshLoading(true)
        assertTrue(tracker.isRefreshing)
        tracker = tracker.onRefreshLoading(false)
        assertFalse(tracker.isRefreshing)
    }

    @Test
    fun `pulling during an ongoing refresh stops when that refresh ends`() {
        val tracker = PullRefreshTracker().onPull(refreshLoading = true)
        assertTrue(tracker.isRefreshing)
        assertFalse(tracker.onRefreshLoading(false).isRefreshing)
    }

    @Test
    fun `timeout stops the indicator`() {
        assertFalse(PullRefreshTracker().onPull(false).onTimeout().isRefreshing)
    }
}
