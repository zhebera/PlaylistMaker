package com.example.playlistmaker.settings.data.localstorage

import android.content.SharedPreferences
import com.example.playlistmaker.utils.SHARED_PREFERENCES_THEME_KEY


class LocalStorageTheme(private val sharedPrefernces: SharedPreferences){

    fun getTheme(): Boolean{
        return sharedPrefernces.getBoolean(SHARED_PREFERENCES_THEME_KEY, false)
    }
    fun changeTheme(changed: Boolean){

        sharedPrefernces.edit()
            .putBoolean(SHARED_PREFERENCES_THEME_KEY, changed)
            .apply()
    }
}