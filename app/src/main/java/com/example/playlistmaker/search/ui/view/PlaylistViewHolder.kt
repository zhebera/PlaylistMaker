package com.example.playlistmaker.search.ui.view

import com.example.playlistmaker.models.Track
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.utils.dpToPx
import java.text.SimpleDateFormat
import java.util.*

class PlaylistViewHolder(parent: ViewGroup,
                         private val clickListener: SearchAdapter.SearchClickListener
): RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
    .inflate(R.layout.track, parent, false)) {

    private val trackName: TextView = itemView.findViewById(R.id.trackName)
    private val artistName: TextView = itemView.findViewById(R.id.artistName)
    private val trackTime: TextView = itemView.findViewById(R.id.trackTimeTxt)
    private val trackImage: ImageView = itemView.findViewById(R.id.trackImg)

    fun bind(track: Track){
        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text =  SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)

        Glide.with(itemView.context)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.music_note)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2.0F,itemView.context)))
            .into(trackImage)

        itemView.setOnClickListener { clickListener.onTrackClick(track) }
    }
}