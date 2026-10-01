package com.cline.app.bridge

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.annotation.RequiresPermission

/**
 * Manages local network access for the Cline runtime.
 * Handles network connectivity monitoring and local network permissions.
 */
class LocalNetworkAccess(private val context: Context) {

    private val connectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private var networkCallback: ConnectivityManager.NetworkCallback? = null
    private var hasLocalNetworkAccess = false

    /**
     * Check if local network access is available.
     */
    fun hasLocalNetworkAccess(): Boolean = hasLocalNetworkAccess

    /**
     * Request local network access permission.
     */
    @RequiresPermission(android.Manifest.permission.LOCAL_NETWORK)
    fun requestLocalNetworkAccess() {
        if (networkCallback == null) {
            networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    updateLocalNetworkAccess()
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    updateLocalNetworkAccess()
                }

                override fun onCapabilitiesChanged(
                    network: Network,
                    networkCapabilities: NetworkCapabilities
                ) {
                    super.onCapabilitiesChanged(network, networkCapabilities)
                    updateLocalNetworkAccess()
                }
            }

            val networkRequest = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                .addCapability(NetworkCapabilities.NET_CAPABILITY_LOCALNET)
                .build()

            connectivityManager.registerNetworkCallback(networkRequest, networkCallback!!)
        }
        updateLocalNetworkAccess()
    }

    /**
     * Release local network access.
     */
    fun releaseLocalNetworkAccess() {
        networkCallback?.let { callback ->
            connectivityManager.unregisterNetworkCallback(callback)
            networkCallback = null
        }
        hasLocalNetworkAccess = false
    }

    /**
     * Check if the device is connected to a local network.
     */
    private fun updateLocalNetworkAccess() {
        val network = connectivityManager.activeNetwork ?: return
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return

        hasLocalNetworkAccess = capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_LOCALNET
        ) && (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
    }

    /**
     * Check if we can access local network resources.
     */
    fun canAccessLocalNetwork(): Boolean {
        return hasLocalNetworkAccess()
    }

    /**
     * Get the current network type.
     */
    fun getNetworkType(): NetworkType {
        val network = connectivityManager.activeNetwork ?: return NetworkType.NONE
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return NetworkType.NONE

        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.WIFI
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> NetworkType.ETHERNET
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.CELLULAR
            else -> NetworkType.NONE
        }
    }

    enum class NetworkType {
        NONE, WIFI, ETHERNET, CELLULAR
    }
}
