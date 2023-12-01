package com.example.playlistmaker.di

import com.example.playlistmaker.library.domain.db.LibraryInteractor
import com.example.playlistmaker.library.domain.impl.LibraryInteractorImpl
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.db.MediatekaInteractor
import com.example.playlistmaker.player.domain.impl.MediatekaInteractorImpl
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import org.koin.dsl.module

val interactorModule = module {

    single<SearchInteractor> {
        SearchInteractorImpl(get())
    }

    factory<PlayerInteractor> {
        PlayerInteractorImpl(get())
    }

    single<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }

    single<MediatekaInteractor>{
        MediatekaInteractorImpl(get())
    }

    single<LibraryInteractor>{
        LibraryInteractorImpl(get())
    }
}