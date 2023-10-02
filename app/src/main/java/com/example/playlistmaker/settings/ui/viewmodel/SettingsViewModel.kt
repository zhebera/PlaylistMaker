package com.example.playlistmaker.settings.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.settings.domain.api.SettingsInteractor

class SettingsViewModel(private val settingsInteractor: SettingsInteractor) : ViewModel() {

    fun changeTheme(changed: Boolean) {
        settingsInteractor.changeTheme(changed)
    }
}
