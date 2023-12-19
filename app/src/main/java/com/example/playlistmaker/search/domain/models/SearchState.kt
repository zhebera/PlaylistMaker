package com.example.playlistmaker.search.domain.models

import com.example.playlistmaker.models.Track

sealed interface SearchState {
    object Loading : SearchState
    data class Content(val data: List<Track>) : SearchState
    object Error : SearchState
    object Empty : SearchState
}