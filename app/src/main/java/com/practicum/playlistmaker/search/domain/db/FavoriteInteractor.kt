package com.practicum.playlistmaker.search.domain.db

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteInteractor {

    fun favoriteTracks(): Flow<List<Track>>

    fun addTrack(track: Track)

    fun removeTrack(track: Track)


}