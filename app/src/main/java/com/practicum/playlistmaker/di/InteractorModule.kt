package com.practicum.playlistmaker.di


import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.impl.PlaylistInteractorImpl
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.db.FavoriteInteractor
import com.practicum.playlistmaker.search.domain.impl.FavoriteInteractorImpl
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.search.domain.use_case.GetListenerUseCase
import com.practicum.playlistmaker.search.domain.use_case.GetTrackListUseCase
import com.practicum.playlistmaker.search.domain.use_case.SaveHistoryUseCase
import com.practicum.playlistmaker.search.domain.use_case.SaveTrackUseCase
import com.practicum.playlistmaker.search.domain.use_case.UnregListenerSavedTrack
import com.practicum.playlistmaker.settings.domain.use_case.LoadThemeUseCase
import com.practicum.playlistmaker.settings.domain.use_case.SaveThemeUseCase
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val interactorModule = module{

    single<TracksInteractor> {
        TracksInteractorImpl(get())
    }

    single<FavoriteInteractor>{
        FavoriteInteractorImpl(get())
    }

    single<PlaylistInteractor>{
        PlaylistInteractorImpl(get())
    }

    single {
        SaveHistoryUseCase(get())
    }

    single {
        GetListenerUseCase(get())
    }

    single {
        GetTrackListUseCase(get())
    }

    single {
        SaveTrackUseCase(get())
    }

    single {
        UnregListenerSavedTrack(get())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(androidContext(),get())
    }

    single {
        LoadThemeUseCase(get())
    }

    single {
        SaveThemeUseCase(get())
    }
}