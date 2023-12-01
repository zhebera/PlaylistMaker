package com.example.playlistmaker.player.domain.db

import com.example.playlistmaker.models.Track
import kotlinx.coroutines.flow.Flow

interface MediatekaInteractor {

    fun checkTrackInTable(trackId: String): Flow<Boolean>
    suspend fun insertTrackToTable(track: Track)
    suspend fun deleteTrackFromTable(track: Track)
}