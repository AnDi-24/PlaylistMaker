package com.practicum.playlistmaker.search.data.converters

import com.practicum.playlistmaker.media.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media.domain.models.Playlist

class PlaylistDbConverter {

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            playlist.id,
        playlist.title,
        playlist.description,
        playlist.coverImagePath,
        playlist.trackIds,
        playlist.tracksCount)
    }

    fun map(playlist: PlaylistEntity): Playlist {
        return Playlist(
            playlist.id,
            playlist.title,
            playlist.description,
            playlist.coverImagePath,
            playlist.trackIds,
            playlist.tracksCount
        )
    }

}