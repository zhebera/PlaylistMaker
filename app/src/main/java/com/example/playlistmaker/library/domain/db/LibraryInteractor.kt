package com.example.playlistmaker.library.domain.db

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.models.Track
import kotlinx.coroutines.flow.Flow

interface LibraryInteractor {

    fun getAllTracks(): Flow<List<Track>>
    fun getAllPlaylist(): Flow<List<Playlist>>
    suspend fun addPlaylist(playlist: Playlist)
}