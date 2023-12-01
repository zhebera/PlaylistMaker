package com.example.playlistmaker.library.ui.viewmodel.tracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.models.LibraryTrackState
import com.example.playlistmaker.library.domain.db.LibraryInteractor
import com.example.playlistmaker.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LibraryTracksViewModel(
    private val libraryInteractor: LibraryInteractor
): ViewModel() {

    private val _libraryTracks = MutableStateFlow<LibraryTrackState>(LibraryTrackState.NoInitialized)
    val libraryTracks: StateFlow<LibraryTrackState> = _libraryTracks

    fun getData(){
        viewModelScope.launch {
            libraryInteractor.getAllTracks().collect{tracks ->
                renderState(tracks)
            }
        }
    }

    private fun renderState(tracks: List<Track>) {
        viewModelScope.launch {
            if(tracks.isNullOrEmpty())
                _libraryTracks.emit(LibraryTrackState.Empty)
            else
                 _libraryTracks.emit(LibraryTrackState.Content(tracks))
        }
    }
}