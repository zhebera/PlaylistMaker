package com.example.playlistmaker.library.domain.db

import android.net.Uri
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.models.Track
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {

    fun getAllTracks(): Flow<List<Track>>
    fun getAllPlaylist(): Flow<List<Playlist>>
    suspend fun addPlaylist(playlist: Playlist)
}