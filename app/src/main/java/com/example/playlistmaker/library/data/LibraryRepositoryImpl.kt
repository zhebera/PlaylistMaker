package com.example.playlistmaker.library.data

import com.example.playlistmaker.data.db.TrackDatabase
import com.example.playlistmaker.library.domain.db.LibraryRepository
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.utils.converters.TrackDbConverter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LibraryRepositoryImpl(
    private val trackDatabase: TrackDatabase,
    private val trackDbConverter: TrackDbConverter
): LibraryRepository {

    override fun getAllTracks(): Flow<List<Track>> = flow {
        val tracks = trackDatabase.trackDao().getAllTracksFromTable()
        emit(trackDbConverter.map(tracks))
    }
}