package com.practicum.playlistmaker.search.domain.repository

import android.content.SharedPreferences
import com.practicum.playlistmaker.search.domain.models.Track

interface SearchHistoryRepository {

    fun createTracksListFromJson(json: String): MutableList<Track>

    fun createTracksFromJson(json: String): Track

    fun createJsonFromTracksList(tracks: MutableList<Track>): String

    fun createJsonFromTrack(track: Track): String

    fun saveHistory(tracks: MutableList<Track>)

    fun saveTrack(track: Track, listener: SharedPreferences.OnSharedPreferenceChangeListener)

    fun getTrack(): Track?

    fun getTrackList(): MutableList<Track>?

    fun getSharedPref(): SharedPreferences
}