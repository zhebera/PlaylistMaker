package com.example.playlistmaker

import android.content.SharedPreferences
import com.example.playlistmaker.utils.consts.SEARCH_HISTORY_NEW_TRACK
import com.example.playlistmaker.utils.createJsonFromTrack

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


