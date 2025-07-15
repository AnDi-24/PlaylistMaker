package com.practicum.playlistmaker.domain.use_case

import com.practicum.playlistmaker.domain.repository.ThemePreferences

class LoadThemeUseCase(private val themePreferences: ThemePreferences) {
    fun execute(): Boolean {
        return themePreferences.loadTheme()
    }
}