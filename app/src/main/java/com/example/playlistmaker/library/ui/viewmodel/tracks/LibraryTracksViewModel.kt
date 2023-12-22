package com.example.playlistmaker.library.ui.viewmodel.tracks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.db.LibraryInteractor
import com.example.playlistmaker.library.domain.models.LibraryTrackState
import com.example.playlistmaker.models.Track
import kotlinx.coroutines.launch

class LibraryTracksViewModel(
    private val libraryInteractor: LibraryInteractor
) : ViewModel() {

    private val _libraryTracks = MutableLiveData<LibraryTrackState>()
    val libraryTracks: LiveData<LibraryTrackState> = _libraryTracks

    fun getData() {
        viewModelScope.launch {
            libraryInteractor.getAllTracks().collect { tracks ->
                renderState(tracks)
            }
        }
    }

    private fun renderState(tracks: List<Track>) {
        if (tracks.isNullOrEmpty())
            _libraryTracks.postValue(LibraryTrackState.Empty)
        else
            _libraryTracks.postValue(LibraryTrackState.Content(tracks))
    }
}