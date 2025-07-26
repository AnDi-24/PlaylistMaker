package com.practicum.playlistmaker.search.domain.use_case

import android.content.SharedPreferences
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository

class SaveTrackUseCase (private val searchHistoryRepository: SearchHistoryRepository) {
    fun execute(track: Track, listener: SharedPreferences.OnSharedPreferenceChangeListener){
        searchHistoryRepository.saveTrack(track, listener)}
}
