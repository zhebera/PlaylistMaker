package com.example.playlistmaker

import Track
import android.content.Intent
import android.content.SharedPreferences
import com.google.gson.Gson

const val KEY_TRACK_ID = "key_track_id"

class PlaylistAdapter(private val searchHistorySharedPref: SharedPreferences): TrackAdapter(){
    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(listTrack[position])

        holder.itemView.setOnClickListener {
            searchHistorySharedPref.edit()
                .putString(SEARCH_HISTORY_NEW_TRACK, createJsonFromTrack(listTrack[position]))
                .apply()

            val audioplayerIntent = Intent(holder.itemView.context, AudioplayerActivity::class.java)
            audioplayerIntent.putExtra(KEY_TRACK_ID, createJsonFromTrack(listTrack[position]))
            holder.itemView.context.startActivity(audioplayerIntent)
        }
    }
}
private fun createJsonFromTrack(track: Track) = Gson().toJson(track)
