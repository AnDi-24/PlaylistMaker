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

    override fun deletePlaylist(playlist: Playlist) {
        playlistRepository.deletePlaylist(playlist)
    }

    override fun saveTrackToPlaylist(track: Track) {
        playlistRepository.saveTrackToPlaylist(track)
    }

    override fun updatePlaylist(playlist: Playlist, trackId: String) {
        playlistRepository.updatePlaylist(playlist, trackId)
    }

    override fun getPlaylistById(id: Int): Flow<Playlist> {
        return playlistRepository.getPlaylistById(id)
    }

    override fun getTracks(idList: List<String>): Flow<List<Track>> {
        return playlistRepository.getTracks(idList)
    }
}