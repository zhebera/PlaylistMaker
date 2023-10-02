package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository): SettingsInteractor {
    override fun changeTheme(changed: Boolean) {
        settingsRepository.changeTheme(changed)
    }

    override fun getTheme(): Boolean {
        return settingsRepository.getTheme()
    }
}