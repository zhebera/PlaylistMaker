package com.example.playlistmaker.creator

import com.example.playlistmaker.data.player.PlayerRepositoryImpl
import com.example.playlistmaker.domain.api.PlayerInteractor
import com.example.playlistmaker.domain.impl.PlayerInteractorImpl

object Creator {

    fun providePlayerInteractor(): PlayerInteractor{
        return PlayerInteractorImpl(PlayerRepositoryImpl())
    }
}