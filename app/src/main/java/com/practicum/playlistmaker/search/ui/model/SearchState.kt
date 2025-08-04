package com.practicum.playlistmaker.search.ui.model


import com.practicum.playlistmaker.search.domain.models.Track

sealed interface SearchState {

    object Loading : SearchState

    data class Content(
        val tracks: List<Track>
    ) : SearchState

    data class Error(
        val additionalMessage: String
    ) : SearchState

    object Empty : SearchState

    data class HistoryActions(
        val track: Track
    ): SearchState

    data class RemoveAt(
        val position: Int
    ): SearchState
}