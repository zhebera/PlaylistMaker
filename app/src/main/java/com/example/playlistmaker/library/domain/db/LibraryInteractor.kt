package com.example.playlistmaker.library.domain.db

import com.example.playlistmaker.models.Playlist
import com.example.playlistmaker.models.Track
import kotlinx.coroutines.flow.Flow

interface LibraryInteractor {

    fun getPlaylist(playlistId: Long): Flow<Playlist>
    fun getAllTracks(): Flow<List<Track>>
    fun getAllPlaylist(): Flow<List<Playlist>>
    suspend fun addPlaylist(playlist: Playlist)
    fun getTracksPlaylist(playlistId: Long): Flow<List<Track>>
    suspend fun deleteTrackFromPlaylist(playlistId: Long, track: Track)
    suspend fun deletePlaylist(playlistId: Long)
    suspend fun updatePlaylist(playlist: Playlist)
}