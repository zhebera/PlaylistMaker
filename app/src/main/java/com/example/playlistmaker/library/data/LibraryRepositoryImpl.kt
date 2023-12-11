package com.example.playlistmaker.library.data

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.library.domain.db.LibraryRepository
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.utils.converters.TrackDbConverter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LibraryRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter
): LibraryRepository {

    override fun getAllTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getAllTracks().reversed()
        emit(trackDbConverter.map(tracks))
    }
}