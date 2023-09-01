package com.example.playlistmaker.domain.api

interface PlayerInteractor {
    fun preparePlayer(url: String,
                      onCompletionPlayer : () -> Unit)

    fun playControl(onPlayerStart : () -> Unit, onPlayerPause : () -> Unit)

    fun startPlayer(onPlayerStart: () -> Unit)

    fun pausePlayer(onPlayerPause: () -> Unit)

    fun release()

    fun getCurrentPosition(): Int
}