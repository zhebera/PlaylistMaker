package com.example.playlistmaker.models

sealed interface PlaylistState {
    object Empty: PlaylistState
    data class Content(val data: List<Playlist>): PlaylistState
}