package com.example.playlistmaker.creator

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.search.data.localstorage.LocalStorage
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.search.data.impl.SearchRepositoryImpl
import com.example.playlistmaker.search.data.network.PlaylistRetrofit
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.search.domain.impl.SearchInteractorImpl
import com.example.playlistmaker.settings.data.impl.SettingsRepositoryImpl
import com.example.playlistmaker.settings.data.localstorage.LocalStorageTheme
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.utils.DARK_THEME_ENABLED
import com.example.playlistmaker.utils.SEARCH_HISTORY_PLAYLIST

object Creator {

    fun providePlayerInteractor(): PlayerInteractor {
        return PlayerInteractorImpl(PlayerRepositoryImpl())
    }

    fun provideSearchInteractor(context: Context): SearchInteractor {
        return SearchInteractorImpl(provideSearchRepository(context))
    }

    fun provideSearchRepository(context: Context): SearchRepository {
        return SearchRepositoryImpl(
            PlaylistRetrofit(context),
            LocalStorage(context.getSharedPreferences(SEARCH_HISTORY_PLAYLIST, MODE_PRIVATE))
        )
    }

    fun provideSettingsInteractor(context: Context): SettingsInteractor {
        return SettingsInteractorImpl(
            SettingsRepositoryImpl(
                LocalStorageTheme(context.getSharedPreferences(DARK_THEME_ENABLED, MODE_PRIVATE))
            )
        )
    }
}