package com.practicum.playlistmaker.data.dto

import com.practicum.playlistmaker.domain.models.Track

class TracksResponse (
    val resultCount: Int,
    val results: List<TrackDto>) : Response()
