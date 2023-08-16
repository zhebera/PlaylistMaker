package com.example.playlistmaker

import android.content.SharedPreferences

class PlaylistAdapter(private val searchHistorySharedPref: SharedPreferences): TrackAdapter(){
    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(listTrack[position])

        holder.itemView.setOnClickListener {
            searchHistorySharedPref.edit()
                .putString(SEARCH_HISTORY_NEW_TRACK, createJsonFromTrack(listTrack[position]))
                .apply()

            openAudioplayerActivity(holder, listTrack[position])
        }
    }
}


