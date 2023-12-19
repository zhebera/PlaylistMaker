package com.example.playlistmaker.models

data class Playlist(
    val id: Long,
    val name: String,
    val overview: String,
    val imageName: String?,
    val tracks: List<String>?
)
