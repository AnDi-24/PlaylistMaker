package com.practicum.playlistmaker.media.data.impl

import android.util.Log
import com.practicum.playlistmaker.media.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media.domain.db.PlaylistRepository
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.search.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.search.data.converters.TrackDbConverter
import com.practicum.playlistmaker.search.data.db.AppDatabase
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackDbConvertor: TrackDbConverter
): PlaylistRepository {
    private val scope = CoroutineScope(Job())

    override  fun savePlaylist(playlist: Playlist) {
        scope.launch {
            appDatabase.playlistDao().insertPlaylist(playlistDbConverter.map(playlist))
        }
    }

    override fun updatePlaylist(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        scope.launch {
            appDatabase.playlistDao().updatePlaylist(
                playlistEntity.id,
                playlistEntity.title,
                playlistEntity.description,
                playlistEntity.coverImagePath,
                playlistEntity.trackIds,
                playlistEntity.tracksCount
            )
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {

            val playlists = appDatabase.playlistDao().getAllPlaylists()
            emit (convertFromPlaylistEntity(playlists))

    }

    override fun saveTrackToPlaylist(track: Track) {
        scope.launch {
            appDatabase.tracksOnPlaylistDao().insertTrack(trackDbConvertor.onPlaylistMap(track))
            Log.d("MyTag", "Трэк: $track")
        }
    }

    private fun convertFromPlaylistEntity(playLists: List<PlaylistEntity>): List<Playlist> {
        return playLists.map { playlist -> playlistDbConverter.map(playlist) }
    }

}