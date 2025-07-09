package com.practicum.playlistmaker.domain.use_case

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.SearchHistoryRepository

class GetSavedTrackUseCase(private val searchHistoryRepository: SearchHistoryRepository) {
    fun execute(): Track? {
        return searchHistoryRepository.getTrack()
    }


}