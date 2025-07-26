package com.practicum.playlistmaker.search.domain.use_case

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository

class GetTrackListUseCase(private val searchHistoryRepository: SearchHistoryRepository) {
    fun execute(): MutableList<Track>? {
        return searchHistoryRepository.getTrackList()
    }
}