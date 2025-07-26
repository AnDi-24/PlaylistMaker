package com.practicum.playlistmaker.util

import android.content.Context
import com.practicum.playlistmaker.search.data.impl.TrackRepositoryImpl
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.impl.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.settings.data.impl.ThemePreferencesImpl
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker.settings.domain.repository.ThemePreferences
import com.practicum.playlistmaker.search.domain.use_case.GetSavedTrackUseCase
import com.practicum.playlistmaker.search.domain.use_case.GetTrackListUseCase
import com.practicum.playlistmaker.settings.domain.use_case.LoadThemeUseCase
import com.practicum.playlistmaker.search.domain.use_case.SaveHistoryUseCase
import com.practicum.playlistmaker.settings.domain.use_case.SaveThemeUseCase
import com.practicum.playlistmaker.search.domain.use_case.SaveTrackUseCase
import com.practicum.playlistmaker.sharing.SharingInteractor
import com.practicum.playlistmaker.sharing.data.impl.ExternalNavigatorImpl
import com.practicum.playlistmaker.sharing.domain.ExternalNavigator
import com.practicum.playlistmaker.sharing.impl.SharingInteractorImpl

object Creator {

    private fun getTracksRepository(context: Context): TracksRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient(context))
    }

    private fun provideSearchHistoryRepository(context: Context): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(context)
    }

    private fun getExternalNavigator(context: Context): ExternalNavigator{
        return ExternalNavigatorImpl(context)
    }

    fun provideThemePreferences(context: Context): ThemePreferences {
        return ThemePreferencesImpl(context)
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    fun provideGetSavedTrack(context: Context): GetSavedTrackUseCase {
        return GetSavedTrackUseCase(provideSearchHistoryRepository(context))
    }

    fun provideGetTrackList(context: Context): GetTrackListUseCase {
        return GetTrackListUseCase(provideSearchHistoryRepository(context))
    }

    fun provideSaveHistory(context: Context): SaveHistoryUseCase {
        return SaveHistoryUseCase(provideSearchHistoryRepository(context))
    }

    fun provideSaveTrack(context: Context): SaveTrackUseCase {
        return SaveTrackUseCase(provideSearchHistoryRepository(context))
    }

    fun provideSaveTheme(context: Context): SaveThemeUseCase {
        return SaveThemeUseCase(provideThemePreferences(context))
    }

    fun provideLoadTheme(context: Context): LoadThemeUseCase {
        return LoadThemeUseCase(provideThemePreferences(context))
    }

    fun provideSharingInteractor(context: Context): SharingInteractor{
        return SharingInteractorImpl(getExternalNavigator(context))
    }

}