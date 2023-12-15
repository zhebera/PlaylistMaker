package com.example.playlistmaker.library.domain.models

import com.example.playlistmaker.models.Track

sealed interface LibraryTrackState{

    object Empty: LibraryTrackState
    data class Content(val data: List<Track>): LibraryTrackState
}