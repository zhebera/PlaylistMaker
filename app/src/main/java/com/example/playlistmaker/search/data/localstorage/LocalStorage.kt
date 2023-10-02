package com.example.playlistmaker.search.data.localstorage

import android.content.SharedPreferences
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.utils.SEARCH_HISTORY_PLAYLIST
import com.example.playlistmaker.utils.createJsonFromListTrack
import com.example.playlistmaker.utils.createListTrackFromJson

class LocalStorage(private val sharedPreferences: SharedPreferences) {

    fun addNewTrackToHistory(track: Track) {
        changeHistory(track = track, remove = false)
    }

    fun removeHistory() {
        changeHistory(track = null, remove = true)
    }

    fun getSavedHistory(): List<Track> {
        return createListTrackFromJson(sharedPreferences.getString(SEARCH_HISTORY_PLAYLIST, null))
    }

    private fun changeHistory(track: Track?, remove: Boolean) {
        val mutableList = getSavedHistory().toMutableList()
        if (remove) mutableList.clear() else {

            var copyIndex:Int? = null
            mutableList.forEachIndexed { index, historyTrack ->
                if(historyTrack.trackId == track?.trackId)
                    copyIndex = index
            }
            if(copyIndex != null) mutableList.removeAt(copyIndex!!)

            mutableList.add(0, track!!)
        }
        if (mutableList.size > 10) mutableList.removeAt(10)
        sharedPreferences.edit()
            .putString(SEARCH_HISTORY_PLAYLIST, createJsonFromListTrack(mutableList)).apply()
    }
}