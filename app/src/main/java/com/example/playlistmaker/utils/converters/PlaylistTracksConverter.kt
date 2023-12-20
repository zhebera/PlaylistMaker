package com.example.playlistmaker.utils.converters

import androidx.room.TypeConverter
import java.util.stream.Collectors

object PlaylistTracksConverter {

    @TypeConverter
    fun fromTracksId(listTracks: List<String>): String =
        listTracks.stream().collect(Collectors.joining(","))

    @TypeConverter
    fun toTracksId(data: String?): List<String>? =
        if (data?.isEmpty() == true)
            listOf()
        else data?.split(",")
}