package com.example.playlistmaker.di

import com.example.playlistmaker.library.ui.viewmodel.playlist.PlaylistCreateViewModel
import com.example.playlistmaker.library.ui.viewmodel.playlist.PlaylistViewModel
import com.example.playlistmaker.library.ui.viewmodel.tracks.LibraryTracksViewModel
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get())
    }

    viewModel {
        PlayerViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get())
    }

    viewModel {
        PlaylistViewModel(get())
    }

    viewModel {
        LibraryTracksViewModel(get())
    }

    viewModel {
        PlaylistCreateViewModel(androidContext(), get())
    }
}