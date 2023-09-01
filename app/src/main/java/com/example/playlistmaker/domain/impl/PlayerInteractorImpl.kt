package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.api.PlayerRepository

class PlayerInteractorImpl(private val playerRepository: PlayerRepository): PlayerInteractor {

    enum class State{
        STATE_DEFAULT,
        STATE_PREPARED,
        STATE_PLAYING,
        STATE_PAUSED
    }

    private var playerState = State.STATE_DEFAULT
    override fun preparePlayer(url: String,
                               onCompletionPlayer : () -> Unit) {
        playerRepository.setDataSource(url)
        playerRepository.prepareAsync()
        playerRepository.getCurrentPosition()
        playerRepository.setOnPreparedListener {
            playerState = State.STATE_PREPARED
        }
        playerRepository.setOnCompletionListener {
            onCompletionPlayer()
            playerState = State.STATE_PREPARED
        }
    }

    override fun playControl(onPlayerStart : () -> Unit, onPlayerPause : () -> Unit) {
        when(playerState){
            State.STATE_PLAYING -> pausePlayer(onPlayerPause)
            State.STATE_PREPARED, State.STATE_PAUSED -> startPlayer(onPlayerStart)
            else -> {}
        }
    }

    override fun startPlayer(onPlayerStart : () -> Unit) {
        playerRepository.start()
        onPlayerStart()
        playerState = State.STATE_PLAYING
    }

    override fun pausePlayer(onPlayerPause: () -> Unit) {
        playerRepository.pause()
        onPlayerPause()
        playerState = State.STATE_PAUSED
    }

    override fun release() {
        playerRepository.release()
    }

    override fun getCurrentPosition(): Int {
        return playerRepository.getCurrentPosition()
    }

}