package com.example.playlistmaker.library.domain.db

import com.example.playlistmaker.models.Playlist
import com.example.playlistmaker.models.Track
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {

    suspend fun getPlaylist(playlistId: Long): Playlist
    fun getAllTracks(): Flow<List<Track>>
    fun getAllPlaylist(): Flow<List<Playlist>>
    suspend fun addPlaylist(playlist: Playlist)
    fun getTracksPlaylist(playlistId: Long): Flow<List<Track>>
}