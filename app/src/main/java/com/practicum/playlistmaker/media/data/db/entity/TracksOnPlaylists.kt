package com.practicum.playlistmaker.media.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks_on_playlists")
data class TracksOnPlaylistsEntity(
    @PrimaryKey
    val trackId: String,
    val artistName: String,
    val artworkUrl100: String,
    val trackName: String,
    val trackTimeMillis: String,
    val releaseDate: String,
    val primaryGenreName: String,
    val country: String,
    val collectionName: String,
    val previewUrl: String
)