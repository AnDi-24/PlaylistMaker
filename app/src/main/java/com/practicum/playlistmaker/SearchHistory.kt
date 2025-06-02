package com.practicum.playlistmaker

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.widget.Toast
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val SEARCH_HISTORY = "search_history"
const val SEARCH_HISTORY_LIST_KEY = "for_search_history_list"
const val SEARCH_HISTORY_KEY = "for_search_history"
lateinit var savedHistory: SharedPreferences
lateinit var listener: OnSharedPreferenceChangeListener
lateinit var historyAdapter: TrackAdapter

class SearchHistory(private val context: Context) {

    private val MAX_HISTORY_SIZE = 10

    private fun removeTrack(track: Track) {
        historyAdapter.savedList.remove(track)
    }

    private fun createTracksListFromJson(json: String): MutableList<Track> {
        val gson = Gson()
        val trackListType = object : TypeToken<List<Track>>() {}.type
        return gson.fromJson(json, trackListType)
    }

    private fun createTracksFromJson(json: String): Track {
        val gson = Gson()
        val trackType = object : TypeToken<Track>() {}.type
        return gson.fromJson(json, trackType)
    }

    private fun createJsonFromTracksList(tracks: MutableList<Track>): String {
        return Gson().toJson(tracks)
    }

    private fun createJsonFromTrack(track: Track): String {
        return Gson().toJson(track)
    }

    fun doHistory() {
        val tracksH = savedHistory.getString(SEARCH_HISTORY_LIST_KEY, null)

        if (tracksH != null) {
            historyAdapter.savedList = createTracksListFromJson(tracksH)
        }

        listener = OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == SEARCH_HISTORY_KEY) {
                val track = savedHistory.getString(SEARCH_HISTORY_KEY, null)
                if (track != null) {
                    removeTrack(createTracksFromJson(track))
                    historyAdapter.savedList.add(0, createTracksFromJson(track))
                    Toast.makeText(context, "Сохранено", Toast.LENGTH_SHORT)
                        .show()
                    if (historyAdapter.savedList.size > MAX_HISTORY_SIZE) {
                        historyAdapter.savedList.removeAt(MAX_HISTORY_SIZE)
                    }
                    historyAdapter.notifyItemInserted(0)
                }
            }
        }
    }

    fun saveHistory() {
        savedHistory.edit {
              putString(SEARCH_HISTORY_LIST_KEY, createJsonFromTracksList(historyAdapter.savedList))
            }
    }

    fun saveTrack(track: Track) {
        savedHistory.edit {
            putString(SEARCH_HISTORY_KEY, createJsonFromTrack(track))
            savedHistory.registerOnSharedPreferenceChangeListener(listener)
        }
    }







}


