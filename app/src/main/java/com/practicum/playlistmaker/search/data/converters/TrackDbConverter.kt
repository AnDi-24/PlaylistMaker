package com.practicum.playlistmaker.search.data.converters

import android.icu.text.SimpleDateFormat
import com.practicum.playlistmaker.media.data.db.entity.TracksOnPlaylistsEntity
import com.practicum.playlistmaker.search.data.db.entity.TrackEntity
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.sharing.domain.model.TrackToShare
import java.util.Locale

class TrackDbConverter {

    fun map(track: Track): TrackEntity {
        return TrackEntity(
            track.trackId,
            track.artistName,
            track.artworkUrl100,
            track.trackName,
            track.trackTimeMillis,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.collectionName,
            track.previewUrl)
    }

    fun onPlaylistMap(track: Track): TracksOnPlaylistsEntity {
        return TracksOnPlaylistsEntity(
            track.trackId,
            track.artistName,
            track.artworkUrl100,
            track.trackName,
            track.trackTimeMillis,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.collectionName,
            track.previewUrl)
    }

    fun trackToShare(track: Track): TrackToShare{
        val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        return TrackToShare(
            track.artistName,
            track.trackName,
            formatter.format(track.trackTimeMillis.toLong())
        )
    }

    fun onTracklistMap(track: TracksOnPlaylistsEntity): Track {
        return Track(
            track.trackId,
            track.artistName,
            track.artworkUrl100,
            track.trackName,
            track.trackTimeMillis,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.collectionName,
            track.previewUrl)
    }

    fun map(track: TrackEntity): Track {
        return Track(track.trackId,
            track.artistName,
            track.artworkUrl100,
            track.trackName,
            track.trackTimeMillis,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.collectionName,
            track.previewUrl)
    }

}