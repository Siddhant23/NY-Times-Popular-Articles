package com.test.android.siddhant.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object Util {
    /**
     * True when the active network claims internet access, whatever its transport
     * (Wi-Fi, cellular, ethernet, VPN, ...).
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java) ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
