package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    fun searchTrack(searchingNameTrack: String): Flow<Pair<List<Track>?, String?>>
    fun addNewTrackToHistory(track: Track)
    fun removeHistory()
    fun getSavedHistory(): List<Track>
}