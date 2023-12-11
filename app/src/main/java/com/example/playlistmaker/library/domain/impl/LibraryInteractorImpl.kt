package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.db.LibraryInteractor
import com.example.playlistmaker.library.domain.db.LibraryRepository
import com.example.playlistmaker.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LibraryInteractorImpl(
    private val libraryRepository: LibraryRepository
): LibraryInteractor {
    override fun getAllTracks(): Flow<List<Track>> {
        return libraryRepository.getAllTracks()
    }
}