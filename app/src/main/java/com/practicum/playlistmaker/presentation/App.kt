package com.practicum.playlistmaker.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.data.ThemePreferences
import com.practicum.playlistmaker.data.dto.SharedPreferencesThemeStorage

const val THEME_CUR = "theme_app"
const val THEME_KEY = "theme_checked"
lateinit var themeSharedPrefs: ThemePreferences

class App : Application(), ThemeSwitcher {

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        themeSharedPrefs = SharedPreferencesThemeStorage(this)
        darkTheme = themeSharedPrefs.loadTheme()

        switchTheme(darkTheme)
    }

    override fun switchTheme(isDarkTheme: Boolean) {
        darkTheme = isDarkTheme
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkTheme) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}