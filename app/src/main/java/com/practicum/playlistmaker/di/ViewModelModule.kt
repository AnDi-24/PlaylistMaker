package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media.ui.FavoriteViewModel
import com.practicum.playlistmaker.media.ui.NewPlaylistViewModel
import com.practicum.playlistmaker.media.ui.PlayListsViewModel
import com.practicum.playlistmaker.player.ui.PlayerViewModel
import com.practicum.playlistmaker.playlist.ui.PlaylistViewModel
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.SearchViewModel
import com.practicum.playlistmaker.settings.ui.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel{
        SearchViewModel(
            get(),
            get(),
            get(),
            get(),
            get(),
            get())
    }

    viewModel{(track : Track) ->
        PlayerViewModel(track, get(), get(), get())
    }

    viewModel {
        SettingsViewModel(
            get(),
            get(),
            get(),
            androidContext()
        )
    }

    viewModel {
        FavoriteViewModel( get())
    }

    viewModel {
        PlayListsViewModel(get ())
    }

    viewModel {
        NewPlaylistViewModel(get(), get())
    }

    viewModel {
        PlaylistViewModel(get (), get())
    }

}