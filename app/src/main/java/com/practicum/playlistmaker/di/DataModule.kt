package com.practicum.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.google.gson.Gson
import com.practicum.playlistmaker.media.data.impl.PlaylistRepositoryImpl
import com.practicum.playlistmaker.media.domain.db.PlaylistRepository
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.impl.FavoriteRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.network.iTunesApi
import com.practicum.playlistmaker.search.domain.db.FavoriteRepository
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker.settings.data.impl.ThemePreferencesImpl
import com.practicum.playlistmaker.settings.domain.repository.ThemePreferences
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

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration(false)
            .build()
    }

    factory { Gson() }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(),get(), get())
    }

    single<FavoriteRepository>{
        FavoriteRepositoryImpl(get(),get())
    }

    single <PlaylistRepository>{
        PlaylistRepositoryImpl(get(), get(), get())
    }

    single<NetworkClient> {
        RetrofitNetworkClient(androidContext(), get())
    }

    factory<ThemePreferences>{
        ThemePreferencesImpl(androidContext()
            .getSharedPreferences("THEME_CUR", Context.MODE_PRIVATE))
    }

    factory { MediaPlayer() }

}

