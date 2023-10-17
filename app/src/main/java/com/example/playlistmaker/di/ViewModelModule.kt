package com.example.playlistmaker.di

import com.example.playlistmaker.library.ui.viewmodel.PlaylistViewModel
import com.example.playlistmaker.library.ui.viewmodel.TracksViewModel
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get())
    }

    viewModel {
        PlayerViewModel(get())
    }

    viewModel {
        SettingsViewModel(get())
    }

    viewModel{
        PlaylistViewModel()
    }

    viewModel{
        TracksViewModel()
    }
}