package com.practicum.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.player.ui.model.PlayerData
import com.practicum.playlistmaker.player.ui.model.PlayerStates
import java.util.Locale

class PlayerViewModel(private val url: String): ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private var playerState = PlayerStates.DEFAULT
    private var timer = "00:00"
    private var timerRunnable: Runnable? = null
    private var mediaPlayer = MediaPlayer()
    private val playerLiveData = MutableLiveData(PlayerData(PlayerStates.DEFAULT, "00:00"))
    fun observePlayer(): LiveData<PlayerData> = playerLiveData

    init {
        preparePlayer()
        timerRunnable = updateTimer()
    }

    override fun onCleared() {
        if(timerRunnable != null) {
            timerRunnable?.let(handler::removeCallbacks)
            timerRunnable = null
        }
        mediaPlayer.release()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = PlayerStates.PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = PlayerStates.PREPARED
            timer = "00:00"
            playerLiveData.postValue(PlayerData(PlayerStates.PREPARED, timer))
        }
    }

    fun startPlayer() {
        timerRunnable?.let(handler::post)
        mediaPlayer.start()
        playerState = PlayerStates.PLAYING
        playerLiveData.postValue(PlayerData(PlayerStates.PLAYING, timer))
    }

    fun pausePlayer() {
        handler.removeCallbacks(timerRunnable!!)
        mediaPlayer.pause()
        playerState = PlayerStates.PAUSED
        playerLiveData.postValue(PlayerData(PlayerStates.PAUSED, timer))
    }

    fun playbackControl() {
        when(playerState) {
            PlayerStates.PLAYING -> {
                pausePlayer()
            }else -> {
            startPlayer()}
        }
    }

    private fun updateTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                val currentPosition =
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                when (playerState) {
                    PlayerStates.PLAYING -> {
                        timer = currentPosition.toString()
                        playerLiveData.postValue(PlayerData(PlayerStates.PLAYING, timer))
                        handler.postDelayed(this, DELAY)
                    }else -> return
                }
            }
        }
    }

    companion object {

        private const val DELAY = 300L

        fun getFactory(url: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(url)
            }
        }
    }
}