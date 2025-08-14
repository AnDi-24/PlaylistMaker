package com.practicum.playlistmaker.settings.data.impl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.practicum.playlistmaker.settings.domain.repository.ThemePreferences


const val THEME_KEY = "theme_checked"
class ThemePreferencesImpl (private val sharedPrefs: SharedPreferences) : ThemePreferences {

    override fun saveTheme(isDarkTheme: Boolean) {
        sharedPrefs.edit {
            putBoolean(THEME_KEY, isDarkTheme)
        }
    }

    override fun loadTheme(): Boolean {
        return sharedPrefs.getBoolean(THEME_KEY, false)
    }
}