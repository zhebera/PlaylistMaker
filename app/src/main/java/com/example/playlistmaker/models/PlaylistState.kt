package com.example.playlistmaker.models

import com.example.playlistmaker.library.domain.models.Playlist

sealed interface PlaylistState {
    object Empty: PlaylistState
    data class Content(val data: List<Playlist>): PlaylistState
}