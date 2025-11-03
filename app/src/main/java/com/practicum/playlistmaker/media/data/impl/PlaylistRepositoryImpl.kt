package com.practicum.playlistmaker.media.data.impl

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.media.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.media.data.db.entity.TracksOnPlaylistsEntity
import com.practicum.playlistmaker.media.domain.db.PlaylistRepository
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.search.data.converters.PlaylistDbConverter
import com.practicum.playlistmaker.search.data.converters.TrackDbConverter
import com.practicum.playlistmaker.search.data.db.AppDatabase
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class PlaylistRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackDbConvertor: TrackDbConverter
): PlaylistRepository {
    private val scope = CoroutineScope(Job())

    val gson = Gson()

    override  fun savePlaylist(playlist: Playlist) {
        scope.launch {
            appDatabase.playlistDao().insertPlaylist(playlistDbConverter.map(playlist))
        }
    }

    override fun deletePlaylist(playlist: Playlist) {
        val tracksOnDeletedPlaylist: List<String> = gson.fromJson(
            playlist.trackIds,
            object : TypeToken<List<String>>() {}.type
        )
        scope.launch {
            appDatabase.playlistDao().deletePlaylist(playlistDbConverter.map(playlist))
            tracksOnDeletedPlaylist.forEach {
                removeTrackFromPlaylists(it)
            }
        }
    }

    override fun updatePlaylist(playlist: Playlist,
                                trackId: String) {
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
            removeTrackFromPlaylists(trackId)
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> = flow {
        val playlists = appDatabase.playlistDao().getAllPlaylists()
        emit (convertFromPlaylistEntity(playlists))

    }

    override fun saveTrackToPlaylist(track: Track) {
        scope.launch {
            appDatabase.tracksOnPlaylistDao().insertTrack(trackDbConvertor.onPlaylistMap(track))
        }
    }

    override fun getPlaylistById(id: Int): Flow<Playlist> = flow {
        val playlist = appDatabase.playlistDao().getPlaylistById(id)
        emit(playlistDbConverter.map(playlist))
    }

    override fun getTracks(idList: List<String>): Flow<List<Track>> = flow{
        val tracks = (appDatabase.tracksOnPlaylistDao().getTracks())
        val matchedTracks = tracks.filter{ track ->
            idList.contains(track.trackId)
        }
        emit(convertFromTracksOnPlaylistEntity(matchedTracks))
    }

    private fun convertFromPlaylistEntity(playLists: List<PlaylistEntity>): List<Playlist> {
        return playLists.map { playlist -> playlistDbConverter.map(playlist) }
    }

    private fun convertFromTracksOnPlaylistEntity(tracks: List<TracksOnPlaylistsEntity>): List<Track>{
        return tracks.map { tracks -> trackDbConvertor.onTracklistMap(tracks)
        }
    }

    private suspend fun removeTrackFromPlaylists(trackId: String) {
        val allPlaylists = getAllPlaylists().first()
        val isInAnyPlaylist = allPlaylists.any { playlist ->
            val trackIds: List<String> = gson.fromJson(
                playlist.trackIds,
                object : TypeToken<List<String>>() {}.type
            )
            trackIds.contains(trackId)
        }
        if (!isInAnyPlaylist) {
            appDatabase.tracksOnPlaylistDao().deleteById(trackId)
        }
    }

}