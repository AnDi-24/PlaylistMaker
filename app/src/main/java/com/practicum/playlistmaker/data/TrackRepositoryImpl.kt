package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.TracksResponse
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.util.Resource

class TrackRepositoryImpl (private val networkClient: NetworkClient) : TracksRepository {

    override fun search(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))

        return when (response.resultCode){
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }
            200 -> {
                val tracksResponse = response as? TracksResponse ?: return Resource.Error("Ошибка сервера")

                if (tracksResponse.results.isEmpty()) {
                    return Resource.Success(emptyList())
                }

                Resource.Success(tracksResponse.results.map {
                    Track(
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
                })
            }
            else -> {
                Resource.Error("Ошибка сервера")
            }
        }

//        return if (response.resultCode == 200) {
//            (response as TracksResponse).results.map {
//                Track(it.artistName,
//                    it.artworkUrl100,
//                    it.trackName,
//                    it.trackTimeMillis,
//                    it.releaseDate,
//                    it.primaryGenreName,
//                    it.country,
//                    it.collectionName,
//                    it.previewUrl) }
//        } else {
//            emptyList()
//        }
    }
}