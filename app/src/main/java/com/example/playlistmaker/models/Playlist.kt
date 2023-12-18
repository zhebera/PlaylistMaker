package com.example.playlistmaker.models

data class Playlist(
    val name: String,
    val overview: String,
    val imageName: String?,
    val tracks: List<String>?
)
