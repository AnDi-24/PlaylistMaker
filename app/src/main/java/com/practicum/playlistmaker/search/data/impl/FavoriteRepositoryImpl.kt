package com.practicum.playlistmaker.search.data.impl


import com.practicum.playlistmaker.search.data.converters.TrackDbConverter
import com.practicum.playlistmaker.search.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.db.entity.TrackEntity
import com.practicum.playlistmaker.search.domain.db.FavoriteRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class FavoriteRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConverter
): FavoriteRepository {

    private val scope = CoroutineScope(Job())


    override fun addTrack(track: Track) {
        scope.launch{
            appDatabase.trackDao().insertTrack(trackDbConvertor.map(track))
        }
    }

    override fun removeTrack(track: Track) {
        scope.launch{
            appDatabase.trackDao().deleteTrack(trackDbConvertor.map(track))
        }
    }

    override fun getFavorites(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getTracks()
        emit(convertFromTrackEntity(tracks))
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }

}