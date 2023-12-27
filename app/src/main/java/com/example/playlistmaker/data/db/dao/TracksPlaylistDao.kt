package com.example.playlistmaker.data.db.dao

import androidx.room.*
import com.example.playlistmaker.data.db.entity.TrackEntity
import com.example.playlistmaker.data.db.entity.TracksPlaylistEntity
import com.example.playlistmaker.utils.converters.TracksPlaylistConverter

@Dao
@TypeConverters(TracksPlaylistConverter::class)
interface TracksPlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrack(trackPlaylistEntity: TracksPlaylistEntity)

    @Query("SELECT * FROM tracks_playlist WHERE playlistId = :playlistId")
    suspend fun getTracks(playlistId: Long): List<TracksPlaylistEntity>

    @Query("DELETE FROM tracks_playlist WHERE playlistId = :playlistId AND track = :track")
    suspend fun deleteTrack(playlistId: Long, track: TrackEntity)

    @Query("DELETE FROM tracks_playlist WHERE playlistId = :playlistId")
    suspend fun deleteTracksByPlaylist(playlistId: Long)
}