package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.player.domain.db.MediatekaInteractor
import com.example.playlistmaker.player.domain.db.MediatekaRepository
import kotlinx.coroutines.flow.Flow

class MediatekaInteractorImpl(
    private val mediatekaRepository: MediatekaRepository
): MediatekaInteractor {
    override fun checkTrack(trackId: String): Flow<Boolean> {
        return mediatekaRepository.checkTrack(trackId)
    }

    override suspend fun addTrack(track: Track) {
        mediatekaRepository.addTrack(track)
    }

    override suspend fun removeTrack(track: Track) {
        mediatekaRepository.removeTrack(track)
    }

    override suspend fun getAllPlaylist(): Flow<List<Playlist>> {
        return mediatekaRepository.getAllPlaylist()
    }
}