package com.example.playlistmaker.library.ui.view.playlist

import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.models.Playlist
import com.example.playlistmaker.utils.PLAYLIST_STORAGE_NAME
import com.example.playlistmaker.utils.converters.rightEnding
import com.example.playlistmaker.utils.dpToPx
import java.io.File

class PlaylistViewHolder(
    parent: ViewGroup,
    layoutRes: Int,
    private val clickListener: PlaylistAdapter.PlaylistClickListener
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context)
        .inflate(layoutRes, parent, false)
) {

    private val playlistName: TextView = itemView.findViewById(R.id.tvPlaylistName)
    private val countSongs: TextView = itemView.findViewById(R.id.tvCountSongs)
    private val imagePlaylist: ImageView = itemView.findViewById(R.id.ivPlaylist)

    fun bind(playlist: Playlist) {
        playlistName.text = playlist.name
        val count =
            if (playlist.tracks.isNullOrEmpty())
                0
            else
                playlist.tracks.size
        countSongs.text = rightEnding(count)
        showImage(playlist.imageName)

        itemView.setOnClickListener { clickListener.onClick(playlist) }
    }

    private fun showImage(name: String?) {
        val filePath = File(itemView.context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), PLAYLIST_STORAGE_NAME)
        val file = File(filePath, name)
        val uri = file.toUri()
        Glide.with(itemView.context)
            .load(uri)
            .placeholder(R.drawable.music_note)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(2.0F, itemView.context)))
            .into(imagePlaylist)
    }
}