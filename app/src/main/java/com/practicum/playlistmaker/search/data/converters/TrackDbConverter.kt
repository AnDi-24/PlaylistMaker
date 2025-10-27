package com.practicum.playlistmaker.search.data.converters

import com.practicum.playlistmaker.media.data.db.entity.TracksOnPlaylistsEntity
import com.practicum.playlistmaker.search.data.db.entity.TrackEntity
import com.practicum.playlistmaker.search.domain.models.Track

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