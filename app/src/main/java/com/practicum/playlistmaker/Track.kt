package com.practicum.playlistmaker

import com.google.gson.annotations.SerializedName
import kotlin.math.max

data class Track(
    @SerializedName("artistName")
    val artistName: String,

    @SerializedName("artworkUrl100")
    val artworkUrl100: String,

    @SerializedName("trackName")
    val trackName: String,

    @SerializedName("trackTimeMillis")
    val trackTimeMillis: String,

    @SerializedName("releaseDate")
    val releaseDate: String,

    @SerializedName("primaryGenreName")
    val primaryGenreName: String,

    @SerializedName("country")
    val country: String,

    @SerializedName("collectionName")
    val collectionName: String

) {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
    fun getReleaseYear() = releaseDate.substring(0, 4)
}

