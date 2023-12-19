package com.example.playlistmaker.library.ui.viewmodel.playlist

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.library.domain.db.LibraryInteractor
import com.example.playlistmaker.models.Playlist

class PlaylistCreateViewModel(private val libraryInteractor: LibraryInteractor) : ViewModel() {

    suspend fun addPlaylist(playlist: Playlist) {
        libraryInteractor.addPlaylist(playlist)
    }
}