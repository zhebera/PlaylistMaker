package com.example.playlistmaker.settings.data.impl

import com.example.playlistmaker.settings.data.localstorage.LocalStorageTheme
import com.example.playlistmaker.settings.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val localStorageTheme: LocalStorageTheme): SettingsRepository {
    override fun changeTheme(changed: Boolean) {
        localStorageTheme.changeTheme(changed)
    }

    override fun getTheme(): Boolean {
        return localStorageTheme.getTheme()
    }
}