package com.example.playlistmaker.player.ui.view

import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.utils.PLAYLIST_STORAGE_NAME
import com.example.playlistmaker.utils.converters.getNameForImage
import com.example.playlistmaker.utils.dpToPx
import java.io.File

class PlaylistViewHolder(
    parent: ViewGroup,
    private val clickListener: PlaylistAdapter.PlaylistClickListener
) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context)
    .inflate(R.layout.playlist, parent, false)) {

    private val playlistName: TextView = itemView.findViewById(R.id.tvPlaylistName)
    private val countSongs: TextView = itemView.findViewById(R.id.tvCountSongs)
    private val imagePlaylist: ImageView = itemView.findViewById(R.id.ivPlaylist)

    fun bind(playlist: Playlist){
        playlistName.text = playlist.name
        countSongs.text = "0 треков"

        val filePath = File(itemView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), PLAYLIST_STORAGE_NAME)
        val file = File(filePath, getNameForImage(playlist.name))
        val uri = file.toURI()
        Glide.with(itemView.context)
            .load(uri)
            .placeholder(R.drawable.music_note)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2.0F,itemView.context)))
            .into(imagePlaylist)

        itemView.setOnClickListener { clickListener.onClick(playlist) }
    }
}