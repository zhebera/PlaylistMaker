package com.example.playlistmaker.settings.domain.api

interface SettingsInteractor {

    fun changeTheme(changed: Boolean)
    fun getTheme(): Boolean
}