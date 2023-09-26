package com.example.playlistmaker.player.domain.presentation

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.player.domain.models.PlayerState

class PlayerViewModel(application: Application): AndroidViewModel(application) {
    companion object{
        private const val REFRESH_TIMER_MILLIS = 300L
        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    private val mediaPlayerInteractor = Creator.providePlayerInteractor()
    private val handler = Handler(Looper.getMainLooper())
    private val timer = setTimer()
    private val trackFinish = getTrackStatusFinishing()

    private val _playerState = MutableLiveData<PlayerState>()
    val playerState: LiveData<PlayerState> = _playerState

    private val _timing = MutableLiveData<Int>()
    val timing: LiveData<Int> = _timing

    private val _finishedPlay = MutableLiveData<Boolean>()
    val finishedPlay: LiveData<Boolean> = _finishedPlay

    private fun renderState(state: PlayerState){
        _playerState.postValue(state)
    }

    fun preparePlayer(trackUrl: String){
        mediaPlayerInteractor.preparePlayer(trackUrl)
    }

    fun pausePlayer(){
        handler.removeCallbacks(timer)
        handler.removeCallbacks(trackFinish)
        mediaPlayerInteractor.pausePlayer()
    }

    fun playControl(){
        mediaPlayerInteractor.playControl()
        handler.post(timer)
        handler.post(trackFinish)
        renderState(mediaPlayerInteractor.getPlayerState())
    }

    fun finishPlay(){
        if(_finishedPlay.value == true){
            handler.removeCallbacks(timer)
            handler.removeCallbacks(trackFinish)
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayerInteractor.release()
    }

    private fun setTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                _timing.postValue(mediaPlayerInteractor.getCurrentPosition())

                handler?.postDelayed(
                    this,
                    REFRESH_TIMER_MILLIS
                )
            }
        }
    }

    private fun getTrackStatusFinishing(): Runnable {
        return object : Runnable {
            override fun run() {
                _finishedPlay.postValue(mediaPlayerInteractor.getPlayerFinish())

                handler?.postDelayed(
                    this,
                    REFRESH_TIMER_MILLIS
                )
            }
        }
    }
}