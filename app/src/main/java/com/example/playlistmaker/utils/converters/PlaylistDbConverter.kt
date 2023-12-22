package com.example.playlistmaker.utils.converters

import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.models.Playlist

class PlaylistDbConverter {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            name = playlist.name,
            overview = playlist.overview,
            imageName = playlist.imageName,
            tracks = playlist.tracks
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            playlistEntity.id,
            playlistEntity.name,
            playlistEntity.overview,
            playlistEntity.imageName,
            playlistEntity.tracks
        )
    }

    fun map(playlistEnity: List<PlaylistEntity>): List<Playlist> {
        return playlistEnity.map { playlistEnity ->
            map(playlistEnity)
        }
    }
}