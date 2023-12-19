package com.example.playlistmaker.utils.converters

import androidx.room.TypeConverter
import java.util.stream.Collectors

object PlaylistTracksConverter {

    @TypeConverter
    fun fromTracksId(listTracks: List<String>?): String? =
        if (listTracks.isNullOrEmpty())
            null
        else
            listTracks.stream().collect(Collectors.joining(","))

    @TypeConverter
    fun toTracksId(data: String?): List<String>? = data?.split(",")
}