package com.example.playlistmaker

import Track
import android.view.RoundedCorner
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class PlaylistViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTime)
    private val trackImage: ImageView = itemView.findViewById(R.id.trackImg)

    fun bind(track: Track){
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = track.trackTime
        Glide.with(itemView.context)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.music_note)
            .centerCrop()
            .transform(RoundedCorners(2))
            .into(trackImage)
    }
}