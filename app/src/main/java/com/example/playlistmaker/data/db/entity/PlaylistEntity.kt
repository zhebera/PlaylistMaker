package com.example.playlistmaker.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.playlistmaker.utils.converters.PlaylistTracksConverter

@Entity(tableName = "playlist_table")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val overview: String,
    val imageName: String,
    @TypeConverters(PlaylistTracksConverter::class)
    val tracks: List<String>
)
