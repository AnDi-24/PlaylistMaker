package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.search.domain.models.Track

interface SearchHistoryRepository {

    fun registerListener(listener: HistoryChangeListener)
    fun unregisterListener()

    fun createTracksListFromJson(json: String): MutableList<Track>

    fun createTracksFromJson(json: String): Track

    fun createJsonFromTracksList(tracks: MutableList<Track>): String

    fun createJsonFromTrack(track: Track): String

    fun saveHistory(tracks: MutableList<Track>)

    fun saveTrack(track: Track)

    fun getTrack(): Track?

    fun getTrackList(): MutableList<Track>?

}