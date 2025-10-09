package com.practicum.playlistmaker.search.data.impl

import com.practicum.playlistmaker.search.domain.api.TracksRepository
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.db.AppDatabase
import com.practicum.playlistmaker.search.data.dto.TracksResponse
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TrackRepositoryImpl (private val networkClient: NetworkClient, private val appDatabase: AppDatabase) : TracksRepository {

    override fun search(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        val favoritesId = appDatabase.trackDao().getTracksId()

        when (response.resultCode){
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }
            200 -> {
                with(response as TracksResponse){
                    val data = response.results.map {
                        Track(
                            it.trackId,
                            it.artistName,
                            it.artworkUrl100,
                            it.trackName,
                            it.trackTimeMillis,
                            it.releaseDate,
                            it.primaryGenreName,
                            it.country,
                            it.collectionName,
                            it.previewUrl
                        )
                    }

                    val tracksWithFavoriteStatus = data.map { track ->
                        track.isFavorite = track.trackId in favoritesId
                        track
                    }
                    emit(Resource.Success(tracksWithFavoriteStatus))
                }
            }
            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }
}