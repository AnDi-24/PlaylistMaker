package com.practicum.playlistmaker.media.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.practicum.playlistmaker.media.data.db.entity.PlaylistEntity

@Dao
interface PlaylistDao {

    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Query("UPDATE playlists SET title = :title, description = :description, coverImagePath = :coverImagePath, trackIds = :trackIds, tracksCount = :tracksCount WHERE id = :id")
    suspend fun updatePlaylist(
        id: Int,
        title: String,
        description: String,
        coverImagePath: String,
        trackIds: String,
        tracksCount: Int
    )

    @Query("SELECT * FROM playlists")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

}