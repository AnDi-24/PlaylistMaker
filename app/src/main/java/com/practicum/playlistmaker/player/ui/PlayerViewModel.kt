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
import com.practicum.playlistmaker.util.SingleLiveEvent
import java.util.Locale

class PlayerViewModel(private val url: String): ViewModel() {

    private val handler = Handler(Looper.getMainLooper())
    private var playerState = STATE_DEFAULT
    private var timer = "00:00"
    private var timerRunnable: Runnable? = null
    private var mediaPlayer = MediaPlayer()

    private val play = MutableLiveData<Int>()
    fun observePlay(): LiveData<Int> = play

    private val pause = SingleLiveEvent<Int>()
    fun observePause(): LiveData<Int> = pause

    private val prepare = SingleLiveEvent<Int>()
    fun observePrepare(): LiveData<Int> = prepare

    private val updateTimer = MutableLiveData<String>()
    fun observeUpdateTimer(): LiveData<String> = updateTimer

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
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            timer = "00:00"
            updateTimer.postValue(timer)
            prepare.postValue(playerState)
        }
    }

    fun startPlayer() {
        timerRunnable?.let(handler::post)
        mediaPlayer.start()
        playerState = STATE_PLAYING
        play.postValue(playerState)
    }

    fun pausePlayer() {
        handler.removeCallbacks(timerRunnable!!)
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        pause.postValue(playerState)
    }

    fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun updateTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                var currentPosition =
                    SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
                when (playerState) {
                    STATE_PLAYING -> {
                        timer = currentPosition.toString()
                        updateTimer.postValue(timer)
                        handler.postDelayed(this, DELAY)
                    }
                }
            }
        }
    }

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 300L

        fun getFactory(url: String): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(url)
            }
        }
    }
}