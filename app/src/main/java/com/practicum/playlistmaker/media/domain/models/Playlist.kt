package com.practicum.playlistmaker.media.domain.models

data class Playlist(
    val id: Int,
    val title: String,
    val description: String,
    val coverImagePath: String,
    var trackIds: String,
    var tracksCount: Int
)
