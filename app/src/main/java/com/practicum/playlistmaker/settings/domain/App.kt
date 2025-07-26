package com.practicum.playlistmaker.settings.domain

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.util.Creator
import com.practicum.playlistmaker.settings.domain.use_case.LoadThemeUseCase

lateinit var themePreferences: LoadThemeUseCase

class App : Application() {

    private var darkTheme = false

    override fun onCreate() {
        super.onCreate()
        themePreferences = Creator.provideLoadTheme(this)
        darkTheme = themePreferences.execute()
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        switchTheme(darkTheme)
    }

    fun switchTheme(isDarkTheme: Boolean) {
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


