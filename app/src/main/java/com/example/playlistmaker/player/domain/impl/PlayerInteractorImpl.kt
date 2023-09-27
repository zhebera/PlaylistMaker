package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.models.PlayerState

class PlayerInteractorImpl(private val playerRepository: PlayerRepository) : PlayerInteractor {

    private var playerState = PlayerState.STATE_DEFAULT
    private var playerFinish: Boolean = false

    override fun getPlayerState(): PlayerState {
        return playerState
    }

    override fun getPlayerFinish(): Boolean {
        return playerFinish
    }

    override fun preparePlayer(url: String) {
        with(playerRepository) {
            setDataSource(url)
            prepareAsync()
            getCurrentPosition()
            setOnPreparedListener {
                playerState = PlayerState.STATE_PREPARED
            }
            setOnCompletionListener {
                playerState = PlayerState.STATE_PREPARED
                playerFinish = true
            }
        }
    }

    override fun playControl() {
        when (playerState) {
            PlayerState.STATE_PLAYING -> pausePlayer()
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> startPlayer()
            else -> Unit
        }
    }

    override fun startPlayer() {
        playerRepository.start()
        playerFinish = false
        playerState = PlayerState.STATE_PLAYING
    }

    override fun pausePlayer() {
        playerRepository.pause()
        playerFinish = false
        playerState = PlayerState.STATE_PAUSED
    }

    override fun release() {
        playerRepository.release()
    }

    override fun getCurrentPosition(): Int {
        return playerRepository.getCurrentPosition()
    }
}