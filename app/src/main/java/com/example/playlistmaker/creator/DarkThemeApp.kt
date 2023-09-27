package com.example.playlistmaker.creator

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.settings.domain.api.SettingsInteractor

class DarkThemeApp: Application(){

    var darkTheme: Boolean = false
    private lateinit var settingsInteractor: SettingsInteractor

    override fun onCreate() {
        super.onCreate()
        settingsInteractor = Creator.provideSettingsInteractor(this)
        darkTheme = settingsInteractor.getTheme()
        //val sharedPreferences = getSharedPreferences(DARK_THEME_ENABLED, MODE_PRIVATE)
        //darkTheme = sharedPreferences.getBoolean(SHARED_PREFERENCES_THEME_KEY, false)
        switchDarkTheme(darkTheme)
    }

    fun switchDarkTheme(darkThemeEnabled: Boolean){
        darkTheme = darkThemeEnabled

        AppCompatDelegate.setDefaultNightMode(
            if(darkThemeEnabled)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}