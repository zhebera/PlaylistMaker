package com.example.playlistmaker.di

import android.content.Context.MODE_PRIVATE
import android.media.MediaPlayer
import androidx.room.Room
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.search.data.localstorage.LocalStorage
import com.example.playlistmaker.search.data.network.ITunesApi
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.PlaylistRetrofit
import com.example.playlistmaker.settings.data.localstorage.LocalStorageTheme
import com.example.playlistmaker.utils.BASE_URL
import com.example.playlistmaker.utils.DARK_THEME_ENABLED
import com.example.playlistmaker.utils.SEARCH_HISTORY_PLAYLIST
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    single<NetworkClient> {
        PlaylistRetrofit(get(), get())
    }

    single(named(SEARCH_HISTORY_PLAYLIST)) {
        androidContext()
            .getSharedPreferences(SEARCH_HISTORY_PLAYLIST, MODE_PRIVATE)
    }

    single<LocalStorage> {
        LocalStorage(get(named(SEARCH_HISTORY_PLAYLIST)))
    }

    factory {
        MediaPlayer()
    }

    single(named(DARK_THEME_ENABLED)) {
        androidContext()
            .getSharedPreferences(DARK_THEME_ENABLED, MODE_PRIVATE)
    }

    single<LocalStorageTheme> {
        LocalStorageTheme(get(named(DARK_THEME_ENABLED)))
    }

    single<AppDatabase>{
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .build()
    }

}