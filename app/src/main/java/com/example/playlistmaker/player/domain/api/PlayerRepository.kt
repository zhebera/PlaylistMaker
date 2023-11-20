package com.example.playlistmaker.player.domain.api

interface PlayerRepository {
    fun setDataSource(url: String)
    fun prepareAsync()
    fun getCurrentPosition(): Int
    fun setOnPreparedListener(onPreparedListener: () -> Unit)
    fun setOnCompletionListener(onCompletionListener: () -> Unit)
    fun start()
    fun pause()
    fun release()
}