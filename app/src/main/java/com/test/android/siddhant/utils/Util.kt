package com.test.android.siddhant.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object Util {
    /**
     * True when the active network claims internet capability. This covers Wi-Fi, cellular,
     * Ethernet, VPN, and other transports alike, instead of allow-listing a few transports.
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
                ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
