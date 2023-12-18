package com.example.playlistmaker.library.ui.viewmodel.playlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.db.LibraryInteractor
import com.example.playlistmaker.models.Playlist
import com.example.playlistmaker.models.PlaylistState
import kotlinx.coroutines.launch

class PlaylistViewModel(private val libraryInteractor: LibraryInteractor): ViewModel() {

    private val _libraryPlaylist = MutableLiveData<PlaylistState>()
    val libraryPlaylist: LiveData<PlaylistState> = _libraryPlaylist

    fun getPlaylists(){
        viewModelScope.launch {
            libraryInteractor.getAllPlaylist().collect{ playlists ->
                renderState(playlists)
            }
        }
    }

    private fun renderState(playlists: List<Playlist>){
        if(playlists.isNullOrEmpty())
            _libraryPlaylist.postValue(PlaylistState.Empty)
        else
            _libraryPlaylist.postValue(PlaylistState.Content(playlists))
    }
}