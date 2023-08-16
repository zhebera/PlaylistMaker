package com.example.playlistmaker

import Track
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

fun createJsonFromTrack(track: Track) = Gson().toJson(track)

fun createTrackFromJson(json: String?) = Gson().fromJson(json, Track::class.java)

fun createListTrackFromJson(json: String?) =
    GsonBuilder().create().fromJson(json, object : TypeToken<MutableList<Track>>() {}.type) ?: mutableListOf<Track>()

fun createJsonFromListTrack(listTrack: List<Track>) = Gson().toJson(listTrack)

