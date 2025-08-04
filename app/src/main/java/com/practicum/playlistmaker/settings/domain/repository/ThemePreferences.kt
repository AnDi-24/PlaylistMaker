package com.practicum.playlistmaker.settings.domain.repository

interface ThemePreferences {
    fun saveTheme(isDarkTheme: Boolean)
    fun loadTheme(): Boolean
}