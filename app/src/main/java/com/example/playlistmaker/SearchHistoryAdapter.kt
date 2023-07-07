package com.example.playlistmaker

import Track
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class SearchHistoryAdapter(searchHistorySharedPref: SharedPreferences): RecyclerView.Adapter<PlaylistViewHolder>() {

    private val json = searchHistorySharedPref.getString(SEARCH_HISTORY_PLAYLIST, null)
    private val listTrack = createTrackFromJson(json)
    var searchHistoryListTrack = listTrack

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.track,parent, false)
        return PlaylistViewHolder(layoutInflater)
    }

    override fun getItemCount() = searchHistoryListTrack.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(searchHistoryListTrack[position])
    }
}

private fun createTrackFromJson(json: String?) =
    GsonBuilder().create().fromJson(json, object : TypeToken<MutableList<Track>>() {}.type) ?: mutableListOf<Track>()

