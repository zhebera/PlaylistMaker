package com.example.playlistmaker.domain.api

import com.example.playlistmaker.utils.player.PlayerState

interface PlayerInteractor {
    fun preparePlayer(url: String)

    fun playControl()

    fun startPlayer()

    fun pausePlayer()

    fun release()

    fun getCurrentPosition(): Int

    fun getPlayerState(): PlayerState

    fun getPlayerFinish(): Boolean
}