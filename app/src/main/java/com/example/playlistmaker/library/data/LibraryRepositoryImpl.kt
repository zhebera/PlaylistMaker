package com.example.playlistmaker.library.data

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.library.domain.db.LibraryRepository
import com.example.playlistmaker.models.Playlist
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.utils.converters.PlaylistDbConverter
import com.example.playlistmaker.utils.converters.TrackDbConverter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class LibraryRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
    private val playlistDbConverter: PlaylistDbConverter
) : LibraryRepository {

    override fun getAllTracks(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getAllTracks().reversed()
        emit(trackDbConverter.map(tracks))
    }

    override fun getAllPlaylist(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getAllPlaylist().reversed()
        emit(playlistDbConverter.map(playlists))
    }

    override suspend fun addPlaylist(playlist: Playlist) {
        appDatabase.playlistDao().addPlaylist(playlistDbConverter.map(playlist))
    }
}