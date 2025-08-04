package com.practicum.playlistmaker.search.domain.use_case

import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository

class UnregListenerSavedTrack(private val searchHistoryRepository: SearchHistoryRepository) {
    fun execute() {
        return searchHistoryRepository.unregisterListener()
    }
}