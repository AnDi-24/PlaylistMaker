package com.practicum.playlistmaker.player.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.main.root.RootActivity
import com.practicum.playlistmaker.player.ui.model.PlayerData
import com.practicum.playlistmaker.player.ui.model.PlayerStates
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.Locale

class MusicService: Service(), AudioPlayerControl {
    private val binder = MusicServiceBinder()

    private var track: Track? = null
    private var trackUrl = ""
    private var trackTitle = ""
    private var artistName = ""

    private var mediaPlayer: MediaPlayer? = null

    inner class MusicServiceBinder : Binder() {
        fun getService(): MusicService = this@MusicService
    }

    private var timerJob: Job? = null
    private var timer = "00:00"

    private val _playerData = MutableStateFlow(PlayerData(PlayerStates.DEFAULT, timer,
        track?.isFavorite ?: false, true))
    val playerData = _playerData.asStateFlow()

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder? {
        if (track == null) {
            track = Json.decodeFromString(intent?.getSerializableExtra("track_extra").toString())
            trackUrl = track?.previewUrl ?: ""
            trackTitle = track?.trackName ?: ""
            artistName = track?.artistName ?: ""
            preparePlayer()
        }
        return binder
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
        stopSelf()
    }

    private fun preparePlayer() {
        _playerData.value = PlayerData(PlayerStates.LOADING, "00:00",
            track?.isFavorite ?: false, true)
        mediaPlayer?.setDataSource(trackUrl)
        mediaPlayer?.prepareAsync()
        mediaPlayer?.setOnPreparedListener {
            _playerData.value = PlayerData(PlayerStates.PREPARED, "00:00",
                track?.isFavorite ?: false, false)
        }
        mediaPlayer?.setOnCompletionListener {
            hideForegroundService()
            timerJob?.cancel()
            _playerData.value = PlayerData(PlayerStates.PREPARED, timer,
                track?.isFavorite ?: false, false)
        }
    }

    override fun startPlayer() {
        mediaPlayer?.start()
        _playerData.value = PlayerData(PlayerStates.PLAYING, getCurrentPlayerPosition(),
            track?.isFavorite ?: false, false)
        updateTimer()
    }

    override fun pausePlayer() {
        hideForegroundService()
        mediaPlayer?.pause()
        timerJob?.cancel()
        _playerData.value = PlayerData(PlayerStates.PAUSED, getCurrentPlayerPosition(),
            track?.isFavorite ?: false, false)
    }

    override fun showForegroundService() {
        if (mediaPlayer?.isPlaying ?: false){
            ServiceCompat.startForeground(
                this,
                SERVICE_NOTIFICATION_ID,
                createNotification(),
                getForegroundServiceTypeConstant()
            )
        }
    }

    override fun hideForegroundService() {
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
    }

    private fun releasePlayer() {
        pausePlayer()
        _playerData.value = PlayerData(PlayerStates.DEFAULT, timer,
            track?.isFavorite ?: false, false)
        mediaPlayer?.stop()
        mediaPlayer?.release()
    }

    override fun getData(): StateFlow<PlayerData> {
        return playerData
    }

    private fun updateTimer() {
        timerJob = CoroutineScope(Dispatchers.Default).launch {
            while (mediaPlayer?.isPlaying == true) {
                delay(DELAY)
                _playerData.value = PlayerData(PlayerStates.PLAYING, getCurrentPlayerPosition(),
                    track?.isFavorite ?: false, false)
            }
        }
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer?.currentPosition) ?: "00:00"
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }

        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Music service",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            setSound(null, null)
        }
        channel.description = "Service for playing music"

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("$artistName - $trackTitle")
            .setSmallIcon(R.drawable.media)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setSound(null)
            .setDefaults(0)
            .setContentIntent(createContentIntent())
            .build()
    }

    private fun getForegroundServiceTypeConstant(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
        } else {
            0
        }
    }

    private fun createContentIntent(): PendingIntent {
        val intent = Intent(this, RootActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                Intent.FLAG_ACTIVITY_SINGLE_TOP

        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        private const val DELAY = 200L
        const val SERVICE_NOTIFICATION_ID = 100
        const val NOTIFICATION_CHANNEL_ID = "music_service_channel"
    }

}