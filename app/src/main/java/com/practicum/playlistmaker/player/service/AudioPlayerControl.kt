package com.practicum.playlistmaker.player.service

import com.practicum.playlistmaker.player.ui.model.PlayerData
import kotlinx.coroutines.flow.StateFlow

interface AudioPlayerControl {
    fun getData(): StateFlow<PlayerData>
    fun startPlayer()
    fun pausePlayer()
    fun showForegroundService()
    fun hideForegroundService()
}