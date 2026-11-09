package com.test.android.siddhant.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowNetworkCapabilities

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class UtilTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val connectivityManager = context.getSystemService(ConnectivityManager::class.java)

    private fun activeNetworkWith(vararg transports: Int, internet: Boolean) {
        val capabilities = ShadowNetworkCapabilities.newInstance()
        transports.forEach { shadowOf(capabilities).addTransportType(it) }
        if (internet) shadowOf(capabilities).addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        shadowOf(connectivityManager).setNetworkCapabilities(connectivityManager.activeNetwork, capabilities)
    }

    @Test
    fun `online when the active network has internet capability`() {
        activeNetworkWith(NetworkCapabilities.TRANSPORT_WIFI, internet = true)

        assertTrue(Util.isNetworkAvailable(context))
    }

    @Test
    fun `VPN-only network with internet counts as online`() {
        activeNetworkWith(NetworkCapabilities.TRANSPORT_VPN, internet = true)

        assertTrue(Util.isNetworkAvailable(context))
    }

    @Test
    fun `offline when the active network lacks internet capability`() {
        activeNetworkWith(NetworkCapabilities.TRANSPORT_WIFI, internet = false)

        assertFalse(Util.isNetworkAvailable(context))
    }

    @Test
    fun `offline when there is no active network`() {
        shadowOf(connectivityManager).setActiveNetworkInfo(null)

        assertFalse(Util.isNetworkAvailable(context))
    }
}
