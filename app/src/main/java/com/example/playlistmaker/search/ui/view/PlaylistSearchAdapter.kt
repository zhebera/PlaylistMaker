package com.example.playlistmaker.search.ui.view

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.models.Track

class PlaylistSearchAdapter(private val clickListener: SearchClickListener) :
    RecyclerView.Adapter<PlaylistSearchViewHolder>() {

    var tracks = ArrayList<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistSearchViewHolder =
        PlaylistSearchViewHolder(parent, clickListener)

    override fun onBindViewHolder(holder: PlaylistSearchViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount() = tracks.size

    interface SearchClickListener {
        fun onTrackClick(track: Track)
        fun onTrackLongClick(track: Track)
    }

}