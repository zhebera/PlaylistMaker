package com.example.playlistmaker.player.data

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.TracksPlaylistEntity
import com.example.playlistmaker.models.Playlist
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.player.domain.db.MediatekaRepository
import com.example.playlistmaker.utils.converters.PlaylistDbConverter
import com.example.playlistmaker.utils.converters.TrackDbConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class MediatekaRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter,
    private val playlistDbConverter: PlaylistDbConverter
) : MediatekaRepository {

    override fun checkTrack(trackId: String) = flow {
        val answer = appDatabase.trackDao().getTrackById(trackId) != null
        emit(answer)
    }

    override suspend fun addTrack(track: Track) {
        withContext(Dispatchers.IO) {
            appDatabase.trackDao().addTrack(trackDbConverter.map(track))
        }
    }

    override suspend fun removeTrack(track: Track) {
        withContext(Dispatchers.IO) {
            appDatabase.trackDao().deleteTrack(trackDbConverter.map(track))
        }
    }

    override fun getAllPlaylist(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getAllPlaylist().reversed()
        emit(playlistDbConverter.map(playlists))
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        withContext(Dispatchers.IO) {
            val listTracksId = ArrayList<String>()
            if (playlist.tracks.isNotEmpty())
                listTracksId.addAll(playlist.tracks)
            listTracksId.add(0, track.trackId)

            appDatabase.playlistDao().updatePlaylistTracksId(
                playlistDbConverter.map(playlist).id,
                listTracksId
            )

            appDatabase.tracksPlaylistDao().addTrack(
                TracksPlaylistEntity(playlistId = playlist.id, track = trackDbConverter.map(track)))
        }
    }
}