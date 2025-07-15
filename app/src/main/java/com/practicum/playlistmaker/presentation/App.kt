package com.practicum.playlistmaker.presentation

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.util.Creator
import com.practicum.playlistmaker.domain.api.ThemeSwitcher
import com.practicum.playlistmaker.domain.use_case.LoadThemeUseCase

lateinit var themePreferences: LoadThemeUseCase

class App : Application(), ThemeSwitcher {

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        themePreferences = Creator.provideLoadTheme(this)
        darkTheme = themePreferences.execute()

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