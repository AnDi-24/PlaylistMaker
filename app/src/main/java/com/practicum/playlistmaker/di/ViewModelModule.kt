package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media.ui.FavoriteViewModel
import com.practicum.playlistmaker.media.ui.PlayListsViewModel
import com.practicum.playlistmaker.player.ui.PlayerViewModel
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

    viewModel{(url: String) ->
        PlayerViewModel(url, get())
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
        FavoriteViewModel()
    }

    viewModel {
        PlayListsViewModel()
    }

}