package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.models.Track
import com.example.playlistmaker.utils.Resource

interface SearchRepository {
    fun searchTrack(searchingNameTrack: String): Resource<List<Track>>
    fun addNewTrackToHistory(track: Track)
    fun removeHistory()
    fun getSavedHistory(): List<Track>
}