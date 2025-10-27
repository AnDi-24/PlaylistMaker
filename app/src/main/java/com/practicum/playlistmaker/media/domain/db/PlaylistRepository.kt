package com.practicum.playlistmaker.media.domain.db

import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    fun savePlaylist(playlist: Playlist)

    fun updatePlaylist(playlist: Playlist)

    fun getAllPlaylists(): Flow<List<Playlist>>

    fun saveTrackToPlaylist(track: Track)


}