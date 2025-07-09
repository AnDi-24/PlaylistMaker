package com.practicum.playlistmaker.domain.use_case

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.SearchHistoryRepository

class GetTrackListUseCase(private val searchHistoryRepository: SearchHistoryRepository) {
    fun execute(): MutableList<Track>? {
        return searchHistoryRepository.getTrackList()
    }
}