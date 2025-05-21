package com.practicum.playlistmaker

import com.google.gson.annotations.SerializedName

data class Track(
    @SerializedName("artistName")
    val artistName: String,

    @SerializedName("artworkUrl100")
    val artworkUrl100: String,

    @SerializedName("trackName")
    val trackName: String,

    @SerializedName("trackTimeMillis")
    val trackTimeMillis: String,
)
