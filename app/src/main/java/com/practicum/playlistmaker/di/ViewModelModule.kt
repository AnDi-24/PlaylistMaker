package com.practicum.playlistmaker.di

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

}