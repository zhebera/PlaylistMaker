package com.example.playlistmaker

import Track
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

open class TrackAdapter:RecyclerView.Adapter<PlaylistViewHolder>() {

    var listTrack = mutableListOf<Track>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.track, parent, false)
        return PlaylistViewHolder(layoutInflater)
    }

    override fun getItemCount() = listTrack.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(listTrack[position])
    }
}