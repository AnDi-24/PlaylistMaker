package com.practicum.playlistmaker.media.domain.db

import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistInteractor {

    fun savePlaylist(playlist: Playlist)

    fun deletePlaylist(playlist: Playlist)

    fun getAllPlaylists(): Flow<List<Playlist>>

    fun saveTrackToPlaylist(track: Track)

    fun  updatePlaylist(playlist: Playlist, trackId: String)

    fun getPlaylistById(id: Int): Flow<Playlist>

    fun getTracks(idList: List<String>): Flow<List<Track>>

}