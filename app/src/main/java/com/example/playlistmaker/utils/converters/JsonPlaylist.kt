package com.example.playlistmaker.utils.converters

import com.example.playlistmaker.models.Playlist
import com.google.gson.Gson

fun createJsonFromPlaylist(playlist: Playlist) = Gson().toJson(playlist)

fun createPlaylistFromJson(json: String?) = Gson().fromJson(json, Playlist::class.java)