package com.practicum.playlistmaker.ui.tracks.model

import android.graphics.drawable.Drawable
import com.practicum.playlistmaker.domain.models.Track

sealed interface TracksState {

    object Loading : TracksState

    data class Content(
        val tracks: List<Track>
    ) : TracksState

    data class Error(
        val errorMessage: String,
        val icon: Drawable?,
        val additionalMessage: String
    ) : TracksState

    data class Empty(
        val emptyMessage: String,
        val icon: Drawable?
    ) : TracksState
}