package com.example.playlistmaker.library.data

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.library.domain.db.LibraryRepository
import com.example.playlistmaker.models.Playlist
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.utils.converters.PlaylistDbConverter
import com.example.playlistmaker.utils.converters.TrackDbConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class LibraryRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
    private val playlistDbConverter: PlaylistDbConverter
) : LibraryRepository {

    override suspend fun getPlaylist(playlistId: Long): Playlist{
        val playlist = appDatabase.playlistDao().getPlaylistById(playlistId)
        return playlistDbConverter.map(playlist)
    }

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

    override fun getTracksPlaylist(playlistId: Long): Flow<List<Track>> = flow{
        val tracksPlaylistEntityList = appDatabase.tracksPlaylistDao().getTracks(playlistId)
        val tracks = tracksPlaylistEntityList.map { it.track }
        emit(trackDbConverter.map(tracks))
    }

    override suspend fun deleteTrackFromPlaylist(playlistId: Long, track: Track) {
        withContext(Dispatchers.IO){
            val listTracksId = ArrayList<String>()
            val listTracks = appDatabase.playlistDao().getPlaylistById(playlistId).tracks.toMutableList()
            listTracks.remove(track.trackId)
            listTracksId.addAll(listTracks.toList())
            appDatabase.playlistDao().updatePlaylistTracksId(playlistId, listTracksId)

            appDatabase.tracksPlaylistDao().deleteTrack(playlistId, trackDbConverter.map(track))
        }
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        appDatabase.playlistDao().deletePlaylist(playlistId)
        appDatabase.tracksPlaylistDao().deleteTracksByPlaylist(playlistId)
    }
}