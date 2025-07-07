package com.practicum.playlistmaker.data.dto

import android.content.Context
import android.content.Context.MODE_PRIVATE
import androidx.core.content.edit
import com.practicum.playlistmaker.data.ThemePreferences
import com.practicum.playlistmaker.presentation.THEME_CUR
import com.practicum.playlistmaker.presentation.THEME_KEY

class SharedPreferencesThemeStorage (context: Context) : ThemePreferences {
    private val sharedPrefs = context.getSharedPreferences(THEME_CUR, MODE_PRIVATE)

    override fun saveTheme(isDarkTheme: Boolean) {
        sharedPrefs.edit {
            putBoolean(THEME_KEY, isDarkTheme)
        }
    }

    override fun loadTheme(): Boolean {
        return sharedPrefs.getBoolean(THEME_KEY, false)
    }
}