package com.practicum.playlistmaker.search.data.impl

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.repository.HistoryChangeListener
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository

const val SEARCH_HISTORY = "search_history"
const val SEARCH_HISTORY_LIST_KEY = "for_search_history_list"
const val SEARCH_HISTORY_KEY = "for_search_history"

class SearchHistoryRepositoryImpl(context: Context): SearchHistoryRepository {

    private val savedHistory = context.getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE)
    private val gson = Gson()
    private var listener: HistoryChangeListener? = null
    private var preferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener? = null

    override fun registerListener(listener: HistoryChangeListener) {
        this.listener = listener
        initPreferenceChangeListener()
    }

    override fun unregisterListener() {
        savedHistory.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        listener = null
        preferenceChangeListener = null
    }
    private fun initPreferenceChangeListener() {
        preferenceChangeListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            if (key == "for_search_history") {
                val track = getTrack()
                listener?.onHistoryUpdated(track)
            }
        }
        savedHistory.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun createTracksListFromJson(json: String): MutableList<Track> {
        val trackListType = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, trackListType)
    }

    override fun createTracksFromJson(json: String): Track {
        val trackType = object : TypeToken<Track>() {}.type
        return gson.fromJson(json, trackType)
    }

    override fun createJsonFromTracksList(tracks: MutableList<Track>): String {
        return Gson().toJson(tracks)
    }

    override fun createJsonFromTrack(track: Track): String {
        return Gson().toJson(track)
    }

    override fun saveHistory(tracks: MutableList<Track>) {
        savedHistory.edit {
            putString(SEARCH_HISTORY_LIST_KEY, createJsonFromTracksList(tracks))
            apply()
        }
    }

    override fun saveTrack(track: Track) {
        savedHistory.edit {
            putString(SEARCH_HISTORY_KEY, createJsonFromTrack(track))
        }
    }

    override fun getTrack(): Track? {
        val trackString = savedHistory.getString(SEARCH_HISTORY_KEY, null)
        return trackString?.let { createTracksFromJson(it) }
    }

    override fun getTrackList(): MutableList<Track>? {
        val tracksH = savedHistory.getString(SEARCH_HISTORY_LIST_KEY, null)
        return tracksH?.let { createTracksListFromJson(it) }
    }
}