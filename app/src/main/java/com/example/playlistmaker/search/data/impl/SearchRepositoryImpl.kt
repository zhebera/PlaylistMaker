package com.example.playlistmaker.search.data.impl

import com.example.playlistmaker.models.Track
import com.example.playlistmaker.search.data.dto.TrackRequest
import com.example.playlistmaker.search.data.dto.TrackResponse
import com.example.playlistmaker.search.data.mapper.MapFromResponseToListTrack
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.localstorage.LocalStorage
import com.example.playlistmaker.search.domain.api.SearchRepository
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SearchRepositoryImpl(private val networkClient: NetworkClient, private val localStorage: LocalStorage) :
    SearchRepository {
    override fun searchTrack(searchingNameTrack: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackRequest(searchingNameTrack))

        when (response.resultCode) {

            -1 -> {
                emit(Resource.Failure(message = "Нет соединения с интернетом..."))
            }

            200, 201 -> {
                emit(Resource.Success(
                    data =
                    MapFromResponseToListTrack.map(response as TrackResponse)
                ))
            }

            else -> {
                emit(Resource.Failure(message = "Ошибка сервера"))
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