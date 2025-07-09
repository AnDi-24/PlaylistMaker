package com.practicum.playlistmaker.domain.use_case

import com.practicum.playlistmaker.domain.repository.ThemePreferences

class SaveThemeUseCase(private val themePreferences: ThemePreferences) {
    fun execute(isDarkTheme: Boolean){
        return themePreferences.saveTheme(isDarkTheme)
    }
}