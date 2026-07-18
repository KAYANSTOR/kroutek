package com.example.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * NetworkMonitorImpl
 * يراقب حالة الشبكة في الخلفية. يستخدمه Sync Engine و Network Engine لاتخاذ القرارات.
 */
class NetworkMonitorImpl(private val context: Context) : NetworkMonitor {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isOnlineFlow = MutableStateFlow(fetchIsOnline())
    val isOnlineFlow: StateFlow<Boolean> get() = _isOnlineFlow

    override var isOnline: Boolean = fetchIsOnline()
        private set
    override var isMetered: Boolean = fetchIsMetered()
        private set
    override var isWifi: Boolean = fetchIsWifi()
        private set

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            updateState()
        }
        override fun onLost(network: Network) {
            updateState()
        }
        override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
            updateState()
        }
    }

    override fun startMonitoring() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
    }

    override fun stopMonitoring() {
        runCatching { connectivityManager.unregisterNetworkCallback(networkCallback) }
    }

    private fun updateState() {
        isOnline = fetchIsOnline()
        isMetered = fetchIsMetered()
        isWifi = fetchIsWifi()
        _isOnlineFlow.value = isOnline
    }

    private fun fetchIsOnline(): Boolean {
        val caps = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return caps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    private fun fetchIsMetered(): Boolean =
        connectivityManager.isActiveNetworkMetered

    private fun fetchIsWifi(): Boolean {
        val caps = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    }
}
