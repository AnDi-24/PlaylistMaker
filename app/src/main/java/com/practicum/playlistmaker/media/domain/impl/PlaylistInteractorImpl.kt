package com.practicum.playlistmaker.media.domain.impl

import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.db.PlaylistRepository
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistInteractorImpl (private val playlistRepository: PlaylistRepository): PlaylistInteractor {

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistRepository.getAllPlaylists()
    }

    override fun savePlaylist(playlist: Playlist) {
        playlistRepository.savePlaylist(playlist)
    }

    override fun saveTrackToPlaylist(track: Track) {
        playlistRepository.saveTrackToPlaylist(track)
    }

    override fun updatePlaylist(playlist: Playlist) {
        playlistRepository.updatePlaylist(playlist)
    }
}