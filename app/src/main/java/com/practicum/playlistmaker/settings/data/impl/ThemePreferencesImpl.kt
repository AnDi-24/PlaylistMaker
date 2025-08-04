package com.practicum.playlistmaker.settings.data.impl

import android.content.Context
import androidx.core.content.edit
import com.practicum.playlistmaker.settings.domain.repository.ThemePreferences

const val THEME_CUR = "theme_app"
const val THEME_KEY = "theme_checked"
class ThemePreferencesImpl (context: Context) : ThemePreferences {
    private val sharedPrefs = context.getSharedPreferences(THEME_CUR, Context.MODE_PRIVATE)

    override fun saveTheme(isDarkTheme: Boolean) {
        sharedPrefs.edit {
            putBoolean(THEME_KEY, isDarkTheme)
        }
    }

    override fun loadTheme(): Boolean {
        return sharedPrefs.getBoolean(THEME_KEY, false)
    }
}