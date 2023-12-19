package com.example.playlistmaker.di

import com.example.playlistmaker.library.data.LibraryRepositoryImpl
import com.example.playlistmaker.library.domain.db.LibraryRepository
import com.example.playlistmaker.player.data.MediatekaRepositoryImpl
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.db.MediatekaRepository
import com.example.playlistmaker.search.data.impl.SearchRepositoryImpl
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.utils.converters.PlaylistDbConverter
import com.example.playlistmaker.utils.converters.TrackDbConverter
import org.koin.dsl.module

val repositoryModule = module {

    factory<TrackDbConverter> {
        TrackDbConverter()
    }

    factory<PlaylistDbConverter> {
        PlaylistDbConverter()
    }

    single<SearchRepository> {
        SearchRepositoryImpl(get(), get())
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(get())
    }

    single<MediatekaRepository> {
        MediatekaRepositoryImpl(get(), get(), get())
    }

    single<LibraryRepository> {
        LibraryRepositoryImpl(get(), get(), get())
    }
}