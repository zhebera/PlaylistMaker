package com.example.playlistmaker.settings.domain.api

interface SettingsRepository {
    fun changeTheme(changed: Boolean)

    fun getTheme(): Boolean
}