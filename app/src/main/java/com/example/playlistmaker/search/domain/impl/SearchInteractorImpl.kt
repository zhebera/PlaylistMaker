package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.models.Track
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.utils.Resource
import java.util.concurrent.Executors

class SearchInteractorImpl(private val searchRepository: SearchRepository) : SearchInteractor {

    private val executor = Executors.newCachedThreadPool()
    override fun searchTrack(searchingNameTrack: String, consumer: SearchInteractor.SearchConsumer) {
        executor.execute {
            when (val response = searchRepository.searchTrack(searchingNameTrack)) {

                is Resource.Failure -> {
                    consumer.consume(null, response.message)
                }

                is Resource.Success -> {
                    consumer.consume(response.data, null)
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