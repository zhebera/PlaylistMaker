package com.example.playlistmaker.library.ui.viewmodel.playlist_description

import android.content.Context
import android.os.Environment
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.db.LibraryInteractor
import com.example.playlistmaker.library.domain.models.LibraryTrackState
import com.example.playlistmaker.models.Playlist
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.utils.PLAYLIST_STORAGE_NAME
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.io.File

class PlaylistDescriptionViewModel(
    context: Context,
    private val libraryInteractor: LibraryInteractor
) : ViewModel() {

    private val _playlistTracks = MutableLiveData<LibraryTrackState>()
    val playlistTracks: LiveData<LibraryTrackState> = _playlistTracks

    private val _playlist = MutableLiveData<Playlist>()
    val playlist: LiveData<Playlist> = _playlist

    private val filePath by lazy {
        File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), PLAYLIST_STORAGE_NAME)
    }

    fun updatePlaylist(playlistId: Long){
        viewModelScope.launch(Dispatchers.IO) {
             libraryInteractor.getPlaylist(playlistId).collect(::updatePlaylist)
        }
    }

    private fun updatePlaylist(playlist: Playlist){
        _playlist.postValue(playlist)
    }

    fun getTracks(playlistId: Long) {
        viewModelScope.launch {
            libraryInteractor.getTracksPlaylist(playlistId).collect(::renderState)
        }
    }

    private fun renderState(tracks: List<Track>){
        if (tracks.isNullOrEmpty())
            _playlistTracks.postValue(LibraryTrackState.Empty)
        else
            _playlistTracks.postValue(LibraryTrackState.Content(tracks))
    }

    fun getImage(name: String?) = File(filePath, name).toUri()

    fun deleteTrackFromPlaylist(playlistId: Long, track: Track){
        viewModelScope.launch {
            libraryInteractor.deleteTrackFromPlaylist(playlistId, track)
            getTracks(playlistId)
        }
    }

    fun deletePlaylist(playlistId: Long){
        viewModelScope.launch {
            libraryInteractor.deletePlaylist(playlistId)
        }
    }
}