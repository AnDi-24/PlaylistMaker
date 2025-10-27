package com.practicum.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.ui.model.PlayerData
import com.practicum.playlistmaker.player.ui.model.PlayerStates
import com.practicum.playlistmaker.search.domain.db.FavoriteInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class PlayerViewModel(private val track: Track,
                      private val mediaPlayer: MediaPlayer,
                      private val favoritesInteractor: FavoriteInteractor)
    : ViewModel() {

    private var playerState = PlayerStates.DEFAULT
    private var timerJob: Job? = null
    private var timer = "00:00"
    private val playerLiveData = MutableLiveData(PlayerData(PlayerStates.DEFAULT, timer, track.isFavorite))
    fun observePlayer(): LiveData<PlayerData> = playerLiveData

    init {
        preparePlayer()
    }

    override fun onCleared() {
        super.onCleared()
        releasePlayer()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = PlayerStates.PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
            playerState = PlayerStates.PREPARED
            playerLiveData.postValue(PlayerData(PlayerStates.PREPARED, timer, track.isFavorite))
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = PlayerStates.PLAYING
        playerLiveData.postValue(PlayerData(PlayerStates.PLAYING, getCurrentPlayerPosition(), track.isFavorite))
        updateTimer()
    }

    fun pausePlayer() {
        mediaPlayer.pause()
        timerJob?.cancel()
        playerState = PlayerStates.PAUSED
        playerLiveData.postValue(PlayerData(PlayerStates.PAUSED, getCurrentPlayerPosition(), track.isFavorite))
    }

    fun playbackControl() {
        when(playerState) {
            PlayerStates.PLAYING -> {
                pausePlayer()
            }PlayerStates.PAUSED, PlayerStates.PREPARED -> {
                startPlayer()
            }else -> { }
        }
    }

    private fun releasePlayer() {
        mediaPlayer.stop()
        mediaPlayer.release()
        playerState = PlayerStates.DEFAULT
    }

    private fun updateTimer() {
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(DELAY)
                playerLiveData.postValue(PlayerData(PlayerStates.PLAYING, getCurrentPlayerPosition(), track.isFavorite))
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition) ?: "00:00"
    }

    fun onFavoriteClicked(track: Track){
        viewModelScope.launch {
            if (!track.isFavorite) {
                favoritesInteractor.addTrack(track)
                track.isFavorite = true
                playerLiveData.postValue(PlayerData(playerState, getCurrentPlayerPosition(), true))


            }else{
                favoritesInteractor.removeTrack(track)
                track.isFavorite = false
                playerLiveData.postValue(PlayerData(playerState, getCurrentPlayerPosition(), false))
            }
        }
    }

    companion object {
        private const val DELAY = 300L
    }
}