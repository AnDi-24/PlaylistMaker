package com.practicum.playlistmaker.data

import com.practicum.playlistmaker.data.dto.TracksResponse
import com.practicum.playlistmaker.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.domain.api.TracksRepository
import com.practicum.playlistmaker.domain.models.Track

class TrackRepositoryImpl (private val networkClient: NetworkClient) : TracksRepository {

    override fun search(expression: String): List<Track> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return if (response.resultCode == 200) {
            (response as TracksResponse).results.map {
                Track(it.artistName, it.artworkUrl100, it.trackName, it.trackTimeMillis, it.releaseDate, it.primaryGenreName, it.country, it.collectionName, it.previewUrl) }
        } else {
            emptyList()
        }
    }
}