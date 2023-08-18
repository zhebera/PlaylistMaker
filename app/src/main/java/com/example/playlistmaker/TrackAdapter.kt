package com.example.playlistmaker

import Track
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

open class TrackAdapter : RecyclerView.Adapter<PlaylistViewHolder>() {
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }


    var listTrack = mutableListOf<Track>()
    private var isClicked = true
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInflater =
            LayoutInflater.from(parent.context).inflate(R.layout.track, parent, false)
        return PlaylistViewHolder(layoutInflater)
    }

    override fun getItemCount() = listTrack.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(listTrack[position])

        holder.itemView.setOnClickListener {
            openAudioplayerActivity(holder, listTrack[position])
        }
    }

    fun clickDebounce(): Boolean {
        val current = isClicked
        if (isClicked) {
            isClicked = false
            handler.postDelayed({ isClicked = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    fun openAudioplayerActivity(holder: PlaylistViewHolder, track: Track) {
        if (clickDebounce()) {
            val audioplayerIntent = Intent(holder.itemView.context, AudioplayerActivity::class.java)
            audioplayerIntent.putExtra(KEY_TRACK_ID, createJsonFromTrack(track))
            holder.itemView.context.startActivity(audioplayerIntent)
        }
    }
}



