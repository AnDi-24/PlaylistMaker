package com.practicum.playlistmaker.search.ui.model

import android.graphics.drawable.Drawable
import com.practicum.playlistmaker.search.domain.models.Track

sealed interface SearchState {

    object Loading : SearchState

    data class Content(
        val tracks: List<Track>
    ) : SearchState

    data class Error(
        val errorMessage: String,
        val icon: Drawable?,
        val additionalMessage: String
    ) : SearchState

    data class Empty(
        val emptyMessage: String,
        val icon: Drawable?
    ) : SearchState

    data class HistoryActions(
        val track: Track
    ): SearchState

    data class RemoveAt(
        val position: Int
    ): SearchState
}