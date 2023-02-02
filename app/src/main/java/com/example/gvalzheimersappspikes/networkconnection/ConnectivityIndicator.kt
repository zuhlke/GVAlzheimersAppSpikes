package com.example.gvalzheimersappspikes.networkconnection

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun ConnectivityIndicator() {
    val context = LocalContext.current
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val networkState = remember {
        mutableStateOf(isWifiConnected(connectivityManager))
    }

    val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network) {
            networkState.value = false
        }

        override fun onAvailable(network: Network) {
            networkState.value = true
        }
    }

    SideEffect {
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    if (!networkState.value) {
        Text(text = "Not connected to Wi-Fi")
    } else {
        Text(text = "Connected to Wi-Fi")
    }
}

fun getCurrentNetworkState(connectivityManager: ConnectivityManager): Boolean {
    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)
    return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}

fun isWifiConnected(connectivityManager: ConnectivityManager): Boolean {
    val network = connectivityManager.activeNetwork
    val capabilities = connectivityManager.getNetworkCapabilities(network)
    return capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
}