package com.practicum.playlistmaker.search.domain.use_case

import com.practicum.playlistmaker.search.domain.repository.HistoryChangeListener
import com.practicum.playlistmaker.search.domain.repository.SearchHistoryRepository

class GetListenerUseCase(private val searchHistoryRepository: SearchHistoryRepository) {
    fun execute(listener: HistoryChangeListener) {
        return searchHistoryRepository.registerListener(listener)
    }
}
