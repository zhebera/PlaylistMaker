package com.example.playlistmaker.search.data.dto

import com.example.playlistmaker.models.Track

data class TrackResponse(
    val resultCount: Int,
    val results: List<Track>
): Response()
