package com.kev.lydia.ui

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.kev.data.network.NetworkObserver
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private val connectivityManager = mockk<ConnectivityManager>()
    private val context = mockk<Context> {
        every { getSystemService(Context.CONNECTIVITY_SERVICE) } returns connectivityManager
    }
    private val callback = slot<ConnectivityManager.NetworkCallback>()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        every { connectivityManager.registerDefaultNetworkCallback(capture(callback)) } just runs
        every { connectivityManager.unregisterNetworkCallback(any<ConnectivityManager.NetworkCallback>()) } just runs
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `isOffline is true when the app starts without network`() = runTest {
        // No default network: the system never calls onAvailable/onLost.
        every { connectivityManager.activeNetwork } returns null
        every { connectivityManager.getNetworkCapabilities(null) } returns null

        val viewModel = MainViewModel(NetworkObserver(context))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.isOffline.collect {} }

        assertTrue(viewModel.isOffline.value)
    }

    @Test
    fun `isOffline goes back to false when the network comes back`() = runTest {
        every { connectivityManager.activeNetwork } returns null
        every { connectivityManager.getNetworkCapabilities(null) } returns null

        val viewModel = MainViewModel(NetworkObserver(context))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.isOffline.collect {} }
        callback.captured.onAvailable(mockk<Network>())

        assertFalse(viewModel.isOffline.value)
    }

    @Test
    fun `isOffline is false when the app starts with internet`() = runTest {
        val network = mockk<Network>()
        val capabilities = mockk<NetworkCapabilities> {
            every { hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) } returns true
        }
        every { connectivityManager.activeNetwork } returns network
        every { connectivityManager.getNetworkCapabilities(network) } returns capabilities

        val viewModel = MainViewModel(NetworkObserver(context))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.isOffline.collect {} }

        assertFalse(viewModel.isOffline.value)
    }
}
