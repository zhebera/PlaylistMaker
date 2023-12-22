package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.db.LibraryInteractor
import com.example.playlistmaker.library.domain.db.LibraryRepository
import com.example.playlistmaker.models.Playlist
import com.example.playlistmaker.models.Track
import kotlinx.coroutines.flow.Flow

class LibraryInteractorImpl(
    private val libraryRepository: LibraryRepository
) : LibraryInteractor {
    override fun getAllTracks(): Flow<List<Track>> {
        return libraryRepository.getAllTracks()
    }

    override fun getAllPlaylist(): Flow<List<Playlist>> {
        return libraryRepository.getAllPlaylist()
    }

    override suspend fun addPlaylist(playlist: Playlist) {
        libraryRepository.addPlaylist(playlist)
    }
}