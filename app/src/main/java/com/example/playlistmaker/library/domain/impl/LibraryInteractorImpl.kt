package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.db.LibraryInteractor
import com.example.playlistmaker.library.domain.db.LibraryRepository
import com.example.playlistmaker.models.Playlist
import com.example.playlistmaker.models.Track
import kotlinx.coroutines.flow.Flow

class LibraryInteractorImpl(
    private val libraryRepository: LibraryRepository
) : LibraryInteractor {

    override fun getPlaylist(playlistId: Long): Flow<Playlist>{
        return libraryRepository.getPlaylist(playlistId)
    }

    override fun getAllTracks(): Flow<List<Track>> {
        return libraryRepository.getAllTracks()
    }

    override fun getAllPlaylist(): Flow<List<Playlist>> {
        return libraryRepository.getAllPlaylist()
    }

    override suspend fun addPlaylist(playlist: Playlist) {
        libraryRepository.addPlaylist(playlist)
    }

    override fun getTracksPlaylist(playlistId: Long): Flow<List<Track>> {
        return libraryRepository.getTracksPlaylist(playlistId)
    }

    override suspend fun deleteTrackFromPlaylist(playlistId: Long, track: Track) {
        libraryRepository.deleteTrackFromPlaylist(playlistId, track)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        libraryRepository.deletePlaylist(playlistId)
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        libraryRepository.updatePlaylist(playlist)
    }
}