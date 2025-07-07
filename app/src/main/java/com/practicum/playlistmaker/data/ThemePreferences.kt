package com.practicum.playlistmaker.data

interface ThemePreferences {
    fun saveTheme(isDarkTheme: Boolean)
    fun loadTheme(): Boolean
}