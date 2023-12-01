package com.example.playlistmaker.player.data

import android.util.Log
import com.example.playlistmaker.data.db.TrackDatabase
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.player.domain.db.MediatekaRepository
import com.example.playlistmaker.utils.converters.TrackDbConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class MediatekaRepositoryImpl(
    private val trackDatabase: TrackDatabase,
    private val trackDbConverter: TrackDbConverter
): MediatekaRepository {

    override fun checkTrackInTable(trackId: String) = flow {
        val answer = trackDatabase.trackDao().getTrackById(trackId) != null
        emit(answer)
    }

    override suspend fun insertTrackToTable(track: Track) {
        withContext(Dispatchers.IO){
            trackDatabase.trackDao().insertTrackToTable(trackDbConverter.map(track))
        }
    }

    override suspend fun deleteTrackFromTable(track: Track) {
        withContext(Dispatchers.IO){
            trackDatabase.trackDao().deleteTrackFromTable(trackDbConverter.map(track))
        }
    }
}