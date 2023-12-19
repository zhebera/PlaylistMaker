package com.example.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.models.Playlist
import com.example.playlistmaker.models.PlaylistState
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.db.MediatekaInteractor
import com.example.playlistmaker.player.domain.models.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val mediaPlayerInteractor: PlayerInteractor,
    private val mediatekaInteractor: MediatekaInteractor
) : ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> = _playerState

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _playlistState = MutableLiveData<PlaylistState>()
    val playlistState: LiveData<PlaylistState> = _playlistState

    private val _showToast = SingleLiveEvent<String?>()
    val showToast: LiveData<String?> = _showToast

    private var timerJob: Job? = null

    private fun renderState(state: PlayerState) {
        _playerState.postValue(state)
    }

    private fun renderFavorite(favorite: Boolean) {
        _isFavorite.postValue(favorite)
    }

    fun preparePlayer(url: String) {
        mediaPlayerInteractor.preparePlayer(url)
        _playerState.postValue(mediaPlayerInteractor.getPlayerState())
    }

    fun playControl() {
        when (mediaPlayerInteractor.getPlayerState()) {
            is PlayerState.Playing -> pausePlayer()
            is PlayerState.Prepared, is PlayerState.Paused -> startPlayer()
            else -> Unit
        }
    }

    fun startPlayer() {
        mediaPlayerInteractor.startPlayer()
        startTimer()
        renderState(PlayerState.Playing(getCurrentPosition()))
    }

    fun pausePlayer() {
        mediaPlayerInteractor.pausePlayer()
        timerJob?.cancel()
        renderState(PlayerState.Paused(getCurrentPosition()))
    }

    fun getCurrentPosition(): String {
        return mediaPlayerInteractor.getCurrentPosition()
    }

    fun releasePlayer() {
        mediaPlayerInteractor.release()
        timerJob?.cancel()
        _playerState.postValue(PlayerState.Default())
    }

    private fun startTimer() {
        viewModelScope.launch {
            timerJob = viewModelScope.launch {
                while (mediaPlayerInteractor.getPlayerState() is PlayerState.Playing) {
                    delay(REFRESH_TIMER_MILLIS)
                    _playerState.postValue(
                        PlayerState.Playing(getCurrentPosition())
                    )
                }
                if (mediaPlayerInteractor.getPlayerState() is PlayerState.Prepared)
                    _playerState.postValue(PlayerState.Prepared())
            }
        }
    }

    fun changeFavourite(track: Track) {
        viewModelScope.launch {
            val favorite = _isFavorite.value ?: false
            if (favorite)
                mediatekaInteractor.removeTrack(track)
            else
                mediatekaInteractor.addTrack(track)
            renderFavorite(!favorite)
        }
    }

    fun checkFavorite(trackId: String) {
        viewModelScope.launch {
            mediatekaInteractor.checkTrack(trackId).collect {
                renderFavorite(it)
            }
        }
    }

    fun getAllPlaylist() {
        viewModelScope.launch {
            mediatekaInteractor.getAllPlaylist().collect { playlists ->
                renderState(playlists)
            }
        }
    }

    private fun renderState(playlists: List<Playlist>) {
        if (playlists.isNullOrEmpty())
            _playlistState.postValue(PlaylistState.Empty)
        else
            _playlistState.postValue(PlaylistState.Content(playlists))
    }

    fun addTrackToPlaylist(playlist: Playlist, trackId: String) {
        viewModelScope.launch {
            if (playlist.tracks?.contains(trackId) == true)
                _showToast.setValue("Трек уже добавлен в плейлист ${playlist.name}")
            else {
                mediatekaInteractor.addTrackToPlaylist(playlist, trackId)
                _showToast.setValue("Добавлено в плейлист ${playlist.name}")
            }

        }
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    companion object {
        private const val REFRESH_TIMER_MILLIS = 300L
    }
}