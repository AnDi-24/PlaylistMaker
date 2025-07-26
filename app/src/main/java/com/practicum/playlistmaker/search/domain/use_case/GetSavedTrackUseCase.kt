package com.practicum.playlistmaker.search.domain.use_case

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository

class GetSavedTrackUseCase(private val searchHistoryRepository: SearchHistoryRepository) {
    fun execute(): Track? {
        return searchHistoryRepository.getTrack()
    }


}