package com.example.playlistmaker.data.db.dao

import androidx.room.*
import com.example.playlistmaker.data.db.entity.PlaylistEntity
import com.example.playlistmaker.utils.converters.PlaylistTracksConverter

@Dao
interface PlaylistDao {
    @Delete
    suspend fun deletePlaylist(playlistEntity: PlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPlaylist(playlistEntity: PlaylistEntity)

    @Query("SELECT * FROM playlist_table")
    suspend fun getAllPlaylist(): List<PlaylistEntity>

    @Query("UPDATE playlist_table SET tracks = :listTracks WHERE id = :playlistId")
    @TypeConverters(PlaylistTracksConverter::class)
    suspend fun updatePlaylistTracksId(playlistId: Long, listTracks: List<String>)
}