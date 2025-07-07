package com.practicum.playlistmaker.data.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TrackDto(
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
    val collectionName: String,

    @SerializedName("previewUrl")
    val previewUrl: String

) : Parcelable {
    fun getCoverArtwork() = artworkUrl100.replaceAfterLast('/',"512x512bb.jpg")
    fun getReleaseYear() = releaseDate.substring(0, 4)
}