package com.practicum.playlistmaker.media.ui.model

import com.practicum.playlistmaker.search.domain.models.Track

sealed interface FavoriteStates {

    data class Content(
        val tracks: List<Track>
    ) : FavoriteStates

    object Empty : FavoriteStates
}