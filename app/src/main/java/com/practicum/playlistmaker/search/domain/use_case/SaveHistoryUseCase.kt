package com.practicum.playlistmaker.search.domain.use_case

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository

class SaveHistoryUseCase(private val searchHistoryRepository: SearchHistoryRepository) {
    fun execute(savedList: MutableList<Track>){
        return searchHistoryRepository.saveHistory(savedList)
    }
}