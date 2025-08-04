package com.practicum.playlistmaker.settings.domain.use_case

import com.practicum.playlistmaker.settings.domain.repository.ThemePreferences

class LoadThemeUseCase(private val themePreferences: ThemePreferences) {
    operator fun invoke(): Boolean = themePreferences.loadTheme()

}