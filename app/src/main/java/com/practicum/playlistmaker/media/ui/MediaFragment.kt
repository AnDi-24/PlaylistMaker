package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class MediaFragment : Fragment() {

    val favViewModel by viewModel<FavoriteViewModel>()
    val playListsViewModel by viewModel<PlayListsViewModel>()
    val settingsViewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {

                MediaCompose(favViewModel, playListsViewModel, settingsViewModel, findNavController())

            }
        }
    }
}
