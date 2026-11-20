package com.kev.lydia.ui.list

import com.kev.lydia.ui.list.components.banner.ConnectivityBanner
import com.kev.lydia.ui.list.components.banner.connectivityBannerFor
import org.junit.Assert.assertEquals
import org.junit.Test

class ConnectivityBannerTest {

    @Test
    fun `offline shows the offline banner`() {
        assertEquals(ConnectivityBanner.Offline, connectivityBannerFor(wasOffline = false, isOffline = true))
        assertEquals(ConnectivityBanner.Offline, connectivityBannerFor(wasOffline = true, isOffline = true))
    }

    @Test
    fun `network coming back confirms it`() {
        assertEquals(ConnectivityBanner.BackOnline, connectivityBannerFor(wasOffline = true, isOffline = false))
    }

    @Test
    fun `staying online shows nothing`() {
        assertEquals(ConnectivityBanner.Hidden, connectivityBannerFor(wasOffline = false, isOffline = false))
    }
}
