package com.example.playlistmaker.data.db.dao

import androidx.room.*
import com.example.playlistmaker.data.db.entity.PlaylistEntity

@Dao
interface PlaylistDao {
    @Delete
    suspend fun deletePlaylist(playlistEntity: PlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity)

    @Query("SELECT * FROM playlist_table")
    suspend fun getAllPlaylist(): List<PlaylistEntity>
}