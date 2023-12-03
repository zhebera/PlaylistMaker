package com.example.playlistmaker.player.domain.impl

import com.example.playlistmaker.player.domain.api.PlayerInteractor
import com.example.playlistmaker.player.domain.api.PlayerRepository
import com.example.playlistmaker.player.domain.models.PlayerState
import java.text.SimpleDateFormat
import java.util.*

class PlayerInteractorImpl(private val playerRepository: PlayerRepository) : PlayerInteractor {

    private var playerState: PlayerState = PlayerState.Default()

    override fun getPlayerState(): PlayerState {
        return playerState
    }

    override fun preparePlayer(url: String) {
        with(playerRepository) {
            setDataSource(url)
            prepareAsync()
            getCurrentPosition()
            setOnPreparedListener {
                playerState = PlayerState.Prepared()
            }
            setOnCompletionListener {
                playerState = PlayerState.Prepared()
            }
        }
    }

    override fun startPlayer() {
        playerRepository.start()
        playerState = PlayerState.Playing(getCurrentPosition())
    }

    override fun pausePlayer() {
        playerRepository.pause()
        playerState = PlayerState.Paused(getCurrentPosition())
    }

    override fun release() {
        playerRepository.release()
        playerState = PlayerState.Default()
    }

    override fun getCurrentPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(playerRepository.getCurrentPosition()) ?: "00:00"
    }
}