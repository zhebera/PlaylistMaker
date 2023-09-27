package com.example.playlistmaker.search.ui.view

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.models.Track

class SearchAdapter(private val clickListener: SearchClickListener): RecyclerView.Adapter<PlaylistViewHolder>() {

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder = PlaylistViewHolder(parent, clickListener)

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount() = tracks.size

    fun interface SearchClickListener{
        fun onTrackClick(track: Track)
    }
}