package com.practicum.playlistmaker.sharing.domain.api

import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.sharing.domain.model.TrackToShare

interface SharingInteractor {
    fun share(content: String)
    fun openTerms()
    fun openSupport()
    fun getTracksToShare(tracks: List<Track>): List<TrackToShare>
}