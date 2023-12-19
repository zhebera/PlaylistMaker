package com.example.playlistmaker.search.data.mapper

import com.example.playlistmaker.models.Track
import com.example.playlistmaker.search.data.dto.TrackResponse

object MapFromResponseToListTrack {

    fun map(response: TrackResponse): List<Track> {
        return response.results.map {
            Track(
                it.trackId,
                it.trackName,
                it.artistName,
                it.trackTimeMillis,
                it.artworkUrl100,
                it.collectionName,
                it.releaseDate,
                it.primaryGenreName,
                it.country,
                it.previewUrl
            )
        }
    }
}