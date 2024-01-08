package com.example.playlistmaker.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.playlistmaker.data.db.dao.PlaylistDao
import com.example.playlistmaker.data.db.dao.TrackDao
import com.example.playlistmaker.data.db.dao.TracksPlaylistDao
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.data.db.entity.TracksPlaylistEntity
import com.example.playlistmaker.utils.converters.PlaylistTracksConverter
import com.example.playlistmaker.utils.converters.TracksPlaylistConverter

@Database(entities = [TrackEntity::class, PlaylistEntity::class, TracksPlaylistEntity::class], version = 1)
@TypeConverters(PlaylistTracksConverter::class, TracksPlaylistConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun tracksPlaylistDao(): TracksPlaylistDao
}