package com.practicum.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.google.gson.Gson
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.impl.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.network.iTunesApi
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker.settings.data.impl.ThemePreferencesImpl
import com.practicum.playlistmaker.settings.domain.repository.ThemePreferences
import com.practicum.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<iTunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(iTunesApi::class.java)
    }

    single {
        androidContext()
            .getSharedPreferences("SEARCH_HISTORY", Context.MODE_PRIVATE)
    }

    factory { Gson() }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(),get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(androidContext(), get())
    }

    factory<ThemePreferences>{
        ThemePreferencesImpl(androidContext()
            .getSharedPreferences("THEME_CUR", Context.MODE_PRIVATE))
    }

    factory <ExternalNavigator>{
        ExternalNavigatorImpl(androidContext())
    }

    factory { MediaPlayer() }
}

