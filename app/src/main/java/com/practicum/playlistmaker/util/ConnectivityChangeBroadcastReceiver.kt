package com.practicum.playlistmaker.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkCapabilities
import android.net.ConnectivityManager
import android.widget.Toast

class ConnectivityChangeBroadcastReceiver: BroadcastReceiver() {

    private var wasConnected = true

    override fun onReceive(context: Context, intent: Intent?) {
        val isConnected = isNetworkAvailable(context)
        if (!isConnected && wasConnected) {
            Toast.makeText(context, "Отсутствует подключение к интернету", Toast.LENGTH_LONG).show()
        }
        wasConnected = isConnected
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }



}