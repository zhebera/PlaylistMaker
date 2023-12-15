package com.example.playlistmaker.player.domain.db

import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.models.Track
import kotlinx.coroutines.flow.Flow

interface MediatekaInteractor {

    fun checkTrack(trackId: String): Flow<Boolean>
    suspend fun addTrack(track: Track)
    suspend fun removeTrack(track: Track)
    suspend fun getAllPlaylist(): Flow<List<Playlist>>
}