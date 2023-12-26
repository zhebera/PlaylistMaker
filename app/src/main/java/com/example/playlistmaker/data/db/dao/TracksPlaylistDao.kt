package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TrackEntity

@Dao
interface TracksPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrack(playlistId: Long, trackEntity: TrackEntity)

    @Query("SELECT * FROM tracks_playlist WHERE playlistId = :playlistId")
    suspend fun getTracks(playlistId: Long): List<TrackEntity>
}