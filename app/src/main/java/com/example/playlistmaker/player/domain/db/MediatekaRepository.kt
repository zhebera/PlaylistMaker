package com.example.playlistmaker.player.domain.db

import com.example.playlistmaker.models.Playlist
import com.example.playlistmaker.models.Track
import kotlinx.coroutines.flow.Flow

interface MediatekaRepository {

    fun checkTrack(trackId: String): Flow<Boolean>
    suspend fun addTrack(track: Track)
    suspend fun removeTrack(track: Track)
    fun getAllPlaylist(): Flow<List<Playlist>>
    suspend fun addTrackToPlaylist(playlist: Playlist, trackId: String)
}