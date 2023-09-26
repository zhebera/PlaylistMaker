package com.example.playlistmaker.creator

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.utils.DARK_THEME_ENABLED
import com.example.playlistmaker.utils.SHARED_PREFERENCES_THEME_KEY

class DarkThemeApp: Application(){

    var darkTheme: Boolean = false

    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = getSharedPreferences(DARK_THEME_ENABLED, MODE_PRIVATE)
        darkTheme = sharedPreferences.getBoolean(SHARED_PREFERENCES_THEME_KEY, false)
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