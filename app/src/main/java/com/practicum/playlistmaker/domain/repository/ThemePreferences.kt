package com.practicum.playlistmaker.domain.repository

interface ThemePreferences {
    fun saveTheme(isDarkTheme: Boolean)
    fun loadTheme(): Boolean
}