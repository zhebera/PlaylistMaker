package com.example.playlistmaker.search.data.impl

import com.example.playlistmaker.models.Track
import com.example.playlistmaker.search.data.dto.TrackRequest
import com.example.playlistmaker.search.data.dto.TrackResponse
import com.example.playlistmaker.search.data.mapper.MapFromResponseToListTrack
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.localstorage.LocalStorage
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.utils.Resource

class SearchRepositoryImpl(private val networkClient: NetworkClient, private val localStorage: LocalStorage) :
    SearchRepository {
    override fun searchTrack(searchingNameTrack: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TrackRequest(searchingNameTrack))

        return when (response.resultCode) {

            -1 -> {
                Resource.Failure(message = "Нет соединения с интернетом...")
            }

            200, 201 -> {
                Resource.Success(
                    data =
                    MapFromResponseToListTrack.map(response as TrackResponse)
                )
            }

            else -> {
                Resource.Failure(message = "Ошибка сервера")
            }
        }
    }

    override fun addNewTrackToHistory(track: Track) {
        localStorage.addNewTrackToHistory(track)
    }

    override fun removeHistory() {
        localStorage.removeHistory()
    }

    override fun getSavedHistory(): List<Track> {
        return localStorage.getSavedHistory()
    }
}