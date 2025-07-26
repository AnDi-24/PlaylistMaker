package com.practicum.playlistmaker.settings.domain.use_case

import com.practicum.playlistmaker.settings.domain.repository.ThemePreferences

class SaveThemeUseCase(private val themePreferences: ThemePreferences) {
    fun execute(isDarkTheme: Boolean){
        return themePreferences.saveTheme(isDarkTheme)
    }
}