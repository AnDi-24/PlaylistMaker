package com.practicum.playlistmaker.domain.use_case

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.repository.SearchHistoryRepository

class SaveHistoryUseCase(private val searchHistoryRepository: SearchHistoryRepository) {
    fun execute(savedList: MutableList<Track>){
        return searchHistoryRepository.saveHistory(savedList)
    }
}