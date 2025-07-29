package com.practicum.playlistmaker.settings.domain.use_case

import com.practicum.playlistmaker.settings.domain.repository.ThemePreferences

class SaveThemeUseCase(private val themePreferences: ThemePreferences) {
    operator fun invoke(isDarkTheme: Boolean) = themePreferences.saveTheme(isDarkTheme)

}