package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.media.data.impl.PlaylistRepositoryImpl
import com.practicum.playlistmaker.media.domain.db.PlaylistRepository
import com.practicum.playlistmaker.search.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.search.data.converters.TrackDbConverter
import com.practicum.playlistmaker.search.data.impl.FavoriteRepositoryImpl
import com.practicum.playlistmaker.search.data.impl.TrackRepositoryImpl
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.db.FavoriteRepository
import com.practicum.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {

    single<TracksRepository> {
        TrackRepositoryImpl(get(), get())
    }

    single <FavoriteRepository> {
        FavoriteRepositoryImpl(get(), get())
    }

    single<PlaylistRepository>{
        PlaylistRepositoryImpl(get(), get(), get())
    }

    factory <ExternalNavigator>{
        ExternalNavigatorImpl(androidContext())
    }

    factory { TrackDbConverter() }

    factory { PlaylistDbConverter() }
}
