package com.example.playlistmaker

import Track
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class PlaylistAdapter(private val searchHistorySharedPref: SharedPreferences) :
    RecyclerView.Adapter<PlaylistViewHolder>() {

    var listTrack = mutableListOf<Track>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.track, parent, false)
        return PlaylistViewHolder(layoutInflater)
    }

    override fun getItemCount() = listTrack.size

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(listTrack[position])

        holder.itemView.setOnClickListener {
            searchHistorySharedPref.edit()
                .putString(SEARCH_HISTORY_NEW_TRACK, createJsonFromTrack(listTrack[position]))
                .apply()
        }
    }
}

private fun createJsonFromTrack(track: Track) = Gson().toJson(track)
