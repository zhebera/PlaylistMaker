package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.models.Track

interface SearchInteractor {
    fun searchTrack(searchingNameTrack: String, consumer: SearchConsumer)

    fun interface SearchConsumer{
        fun consume(tracks: List<Track>?, errorMessage: String?)
    }

    fun addNewTrackToHistory(track: Track)
    fun removeHistory()

    fun getSavedHistory(): List<Track>
}