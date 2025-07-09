package com.practicum.playlistmaker

import android.content.Context
import com.practicum.playlistmaker.data.TrackRepositoryImpl
import com.practicum.playlistmaker.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.data.repository.SearchHistoryRepositoryImpl
import com.practicum.playlistmaker.data.repository.ThemePreferencesImpl
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.impl.TracksInteractorImpl
import com.practicum.playlistmaker.domain.repository.SearchHistoryRepository
import com.practicum.playlistmaker.domain.repository.ThemePreferences
import com.practicum.playlistmaker.domain.use_case.GetSavedTrackUseCase
import com.practicum.playlistmaker.domain.use_case.SaveHistoryUseCase
import com.practicum.playlistmaker.domain.use_case.GetTrackListUseCase
import com.practicum.playlistmaker.domain.use_case.LoadThemeUseCase
import com.practicum.playlistmaker.domain.use_case.SaveThemeUseCase
import com.practicum.playlistmaker.domain.use_case.SaveTrackUseCase

object Creator {

    private fun getTracksRepository(): TracksRepository {
        return TrackRepositoryImpl(RetrofitNetworkClient())
    }

    private fun provideSearchHistoryRepository(context: Context): SearchHistoryRepository{
        return SearchHistoryRepositoryImpl(context)
    }

    private fun provideThemePreferences(context: Context): ThemePreferences{
        return ThemePreferencesImpl(context)
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    fun provideGetSavedTrack(context: Context): GetSavedTrackUseCase{
        return GetSavedTrackUseCase(provideSearchHistoryRepository(context))
    }

    fun provideGetTrackList(context: Context): GetTrackListUseCase {
        return GetTrackListUseCase(provideSearchHistoryRepository(context))
    }

    fun provideSaveHistory(context: Context): SaveHistoryUseCase{
        return SaveHistoryUseCase(provideSearchHistoryRepository(context))
    }

    fun provideSaveTrack(context: Context): SaveTrackUseCase{
        return SaveTrackUseCase(provideSearchHistoryRepository(context))
    }

    fun provideSaveTheme(context: Context): SaveThemeUseCase {
        return SaveThemeUseCase(provideThemePreferences(context))
    }

    fun provideLoadTheme(context: Context): LoadThemeUseCase {
        return LoadThemeUseCase(provideThemePreferences(context))
    }

}