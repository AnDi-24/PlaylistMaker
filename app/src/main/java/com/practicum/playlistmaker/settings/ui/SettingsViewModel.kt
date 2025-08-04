package com.practicum.playlistmaker.settings.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.settings.domain.use_case.LoadThemeUseCase
import com.practicum.playlistmaker.settings.domain.use_case.SaveThemeUseCase
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val loadThemeUseCase: LoadThemeUseCase,
    private val saveThemeUseCase: SaveThemeUseCase,
    private val context: Context

): ViewModel() {

    fun sharingButtonClick(){
        sharingInteractor.shareApp()
    }

    fun supportButtonClick(){
        sharingInteractor.openSupport()
    }

    fun eulaButtonClick(){
        sharingInteractor.openTerms()
    }

    fun switchTheme(checked: Boolean){
        saveThemeUseCase(checked)
        (context as App).switchTheme(loadThemeUseCase())
    }
}