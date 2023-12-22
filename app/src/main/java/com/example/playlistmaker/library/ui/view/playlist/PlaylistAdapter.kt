package com.example.playlistmaker.library.ui.view.playlist

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.models.Playlist


class PlaylistAdapter(private val layoutRes: Int, private val clickListener: PlaylistClickListener) :
    RecyclerView.Adapter<PlaylistViewHolder>() {

    val playlists = ArrayList<Playlist>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PlaylistViewHolder(parent, layoutRes, clickListener)

    override fun getItemCount() = playlists.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    fun interface PlaylistClickListener {
        fun onClick(playlist: Playlist)
    }
}