package com.practicum.playlistmaker.media.ui.model

import com.practicum.playlistmaker.media.domain.models.Playlist

sealed interface PlaylistStates {

    data class Content(
        val playlist: List<Playlist>
    ) : PlaylistStates

    object Empty : PlaylistStates

}