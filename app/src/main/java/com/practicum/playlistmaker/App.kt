package com.practicum.playlistmaker

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

const val THEME_CUR = "theme_app"
const val THEME_KEY = "theme_checked"
lateinit var themeSharedPrefs: SharedPreferences

class App : Application() {

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        themeSharedPrefs = getSharedPreferences(THEME_CUR, MODE_PRIVATE)
        darkTheme = themeSharedPrefs.getBoolean(THEME_KEY, false)

        switchTheme(darkTheme)
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}