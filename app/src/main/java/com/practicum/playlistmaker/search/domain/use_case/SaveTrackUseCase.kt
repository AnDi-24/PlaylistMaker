package com.practicum.playlistmaker.search.domain.use_case

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository

class SaveTrackUseCase (private val searchHistoryRepository: SearchHistoryRepository) {
    fun execute(track: Track){
        searchHistoryRepository.saveTrack(track)}
}
