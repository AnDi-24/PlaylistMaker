package com.practicum.playlistmaker.search.domain.db

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {

    fun addTrack(track: Track)

    fun removeTrack(track:Track)

    fun getFavorites(): Flow<List<Track>>
}