package com.example.playlistmaker.player.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.models.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(private val mediaPlayerInteractor: PlayerInteractor): ViewModel() {

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> = _playerState

    private var timerJob: Job? = null

    private fun renderState(state: PlayerState) {
        _playerState.postValue(state)
    }

    fun preparePlayer(url: String){
        mediaPlayerInteractor.preparePlayer(url)
        _playerState.postValue(mediaPlayerInteractor.getPlayerState())
    }

    fun playControl(){
        when(mediaPlayerInteractor.getPlayerState()){
            is PlayerState.Playing -> pausePlayer()
            is PlayerState.Prepared, is PlayerState.Paused-> startPlayer()
            else -> Unit
        }
    }

    fun startPlayer() {
        mediaPlayerInteractor.startPlayer()
        startTimer()
        renderState(PlayerState.Playing(getCurrentPosition()))
    }

    fun pausePlayer(){
        mediaPlayerInteractor.pausePlayer()
        timerJob?.cancel()
        renderState(PlayerState.Paused(getCurrentPosition()))
    }

    fun getCurrentPosition(): String {
        return mediaPlayerInteractor.getCurrentPosition()
    }

    private fun releasePlayer(){
        mediaPlayerInteractor.release()
        timerJob?.cancel()
        _playerState.postValue(PlayerState.Default())
    }

    private fun startTimer(){
        viewModelScope.launch {
            timerJob = viewModelScope.launch {
                while(mediaPlayerInteractor.getPlayerState() is PlayerState.Playing){
                    delay(REFRESH_TIMER_MILLIS)
                    _playerState.postValue(PlayerState.Playing(getCurrentPosition())
                    )
                }
                if(mediaPlayerInteractor.getPlayerState() is PlayerState.Prepared)
                    _playerState.postValue(PlayerState.Prepared())
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