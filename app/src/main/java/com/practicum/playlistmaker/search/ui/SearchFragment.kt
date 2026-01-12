package com.practicum.playlistmaker.search.ui

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import com.practicum.playlistmaker.util.ConnectivityChangeBroadcastReceiver
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment() {

    private val connectivityChangeBroadcastReceiver = ConnectivityChangeBroadcastReceiver()
    private val viewModel by viewModel<SearchViewModel>()
    private val themeViewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {

                SearchCompose(viewModel, themeViewModel, findNavController())

            }
        }
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(connectivityChangeBroadcastReceiver)
    }

    override fun onResume() {
        super.onResume()
        ContextCompat.registerReceiver(
            requireContext(),
            connectivityChangeBroadcastReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }
}