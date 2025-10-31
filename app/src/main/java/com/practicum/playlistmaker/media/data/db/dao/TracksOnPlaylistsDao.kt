package com.practicum.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.media.data.db.entity.TracksOnPlaylistsEntity

@Dao
interface TracksOnPlaylistsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(track: TracksOnPlaylistsEntity)

    @Query("SELECT * FROM tracks_on_playlists")
    suspend fun getTracks(): List<TracksOnPlaylistsEntity>

    @Query("DELETE  FROM tracks_on_playlists WHERE trackId = :trackId" )
    suspend fun deleteById(trackId: String)

    @Delete
    suspend fun deleteTrack(track: TracksOnPlaylistsEntity)
}