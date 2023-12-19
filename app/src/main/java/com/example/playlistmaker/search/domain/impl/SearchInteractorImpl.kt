package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.models.Track
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchInteractorImpl(private val searchRepository: SearchRepository) : SearchInteractor {

    override fun searchTrack(searchingNameTrack: String): Flow<Pair<List<Track>?, String?>> {
        return searchRepository.searchTrack(searchingNameTrack).map { result ->
            when (result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }

                is Resource.Failure -> {
                    Pair(null, result.message)
                }
            }
        }
    }

    override fun addNewTrackToHistory(track: Track) {
        searchRepository.addNewTrackToHistory(track)
    }

    override fun removeHistory() {
        searchRepository.removeHistory()
    }

    override fun getSavedHistory(): List<Track> {
        return searchRepository.getSavedHistory()
    }
}