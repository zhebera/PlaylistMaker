package com.example.playlistmaker.data.db.dao

import androidx.room.*
import com.example.playlistmaker.data.db.entity.TrackEntity

@Dao
interface TrackDao {

    @Delete
    suspend fun deleteTrack(trackEntity: TrackEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrack(trackEntity: TrackEntity)

    @Query("SELECT * FROM tracks_table")
    suspend fun getAllTracks(): List<TrackEntity>

    @Query("SELECT * FROM tracks_table WHERE trackId = :trackId")
    suspend fun getTrackById(trackId: String): TrackEntity?
}