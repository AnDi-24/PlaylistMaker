package com.practicum.playlistmaker.search.domain.repository

import com.practicum.playlistmaker.search.domain.models.Track

interface HistoryChangeListener {
    fun onHistoryUpdated(track: Track?)
}