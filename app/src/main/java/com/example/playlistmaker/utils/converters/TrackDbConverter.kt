package com.example.playlistmaker.utils.converters

import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.models.Track

class TrackDbConverter {

    fun map(track: Track): TrackEntity{
        return TrackEntity(
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl)
    }

    fun map(trackEntity: TrackEntity): Track{
        return Track(
            trackEntity.trackId,
            trackEntity.trackName,
            trackEntity.artistName,
            trackEntity.trackTimeMillis,
            trackEntity.artworkUrl100,
            trackEntity.collectionName,
            trackEntity.releaseDate,
            trackEntity.primaryGenreName,
            trackEntity.country,
            trackEntity.previewUrl
        )
    }

    fun map(tracksEntity: List<TrackEntity>): List<Track>{
        return tracksEntity.map {trackEntity ->
            map(trackEntity)
        }
    }
}