package com.practicum.playlistmaker.settings.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.sharing.SharingInteractor

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor
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

    companion object{
        fun getFactory(sharingInteractor: SharingInteractor): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(sharingInteractor)
            }
        }
    }
}