package com.example.playlistmaker.domain.models

data class TrackResponse(
    val resultCount: Int,
    val results: List<Track>
)
