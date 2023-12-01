package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.models.Track
import com.example.playlistmaker.player.domain.db.MediatekaInteractor
import com.example.playlistmaker.player.domain.db.MediatekaRepository
import kotlinx.coroutines.flow.Flow

class MediatekaInteractorImpl(
    private val mediatekaRepository: MediatekaRepository
): MediatekaInteractor {
    override fun checkTrackInTable(trackId: String): Flow<Boolean> {
        return mediatekaRepository.checkTrackInTable(trackId)
    }

    override suspend fun insertTrackToTable(track: Track) {
        mediatekaRepository.insertTrackToTable(track)
    }

    override suspend fun deleteTrackFromTable(track: Track) {
        mediatekaRepository.deleteTrackFromTable(track)
    }
}