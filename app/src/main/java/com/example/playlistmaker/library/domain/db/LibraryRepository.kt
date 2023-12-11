package com.example.playlistmaker.library.domain.db

import com.example.playlistmaker.models.Track
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {

    fun getAllTracks(): Flow<List<Track>>
}