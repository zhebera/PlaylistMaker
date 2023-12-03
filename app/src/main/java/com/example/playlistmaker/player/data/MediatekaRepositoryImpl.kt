package com.example.playlistmaker.player.data

import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.player.domain.db.MediatekaRepository
import com.example.playlistmaker.utils.converters.TrackDbConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class MediatekaRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter
): MediatekaRepository {

    override fun checkTrack(trackId: String) = flow {
        val answer = appDatabase.trackDao().getTrackById(trackId) != null
        emit(answer)
    }

    override suspend fun addTrack(track: Track) {
        withContext(Dispatchers.IO){
            appDatabase.trackDao().insertTrack(trackDbConverter.map(track))
        }
    }

    override suspend fun removeTrack(track: Track) {
        withContext(Dispatchers.IO){
            appDatabase.trackDao().deleteTrack(trackDbConverter.map(track))
        }
    }
}