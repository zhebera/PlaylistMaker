package com.example.playlistmaker

import Track
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class SearchHistoryAdapter: RecyclerView.Adapter<PlaylistViewHolder>() {

    var searchHistoryListTrack = mutableListOf<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.track,parent, false)
        return PlaylistViewHolder(layoutInflater)
    }

    override fun getItemCount() = searchHistoryListTrack.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(searchHistoryListTrack[position])
    }
}
