package com.practicum.playlistmaker

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 300L
    }

    private val handler = Handler(Looper.getMainLooper())

    private var playerState = STATE_DEFAULT
    private lateinit var cover: ImageView
    private lateinit var trackName: TextView
    private lateinit var artist: TextView
    private lateinit var progressTime: TextView
    private lateinit var duration: TextView
    private lateinit var albumName: TextView
    private lateinit var yearName: TextView
    private lateinit var genreName: TextView
    private lateinit var countryName: TextView
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var chosenTrack: Track
    private lateinit var url: String
    private var mediaPlayer = MediaPlayer()

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.visibility = View.VISIBLE
            pauseButton.visibility = View.GONE
            progressTime.text = getString(R.string.time_example)
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {
        handler.post(updateTimer())
        mediaPlayer.start()
        playButton.visibility = View.GONE
        pauseButton.visibility = View.VISIBLE
        pauseButton.isEnabled = true
        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.visibility = View.VISIBLE
        pauseButton.visibility = View.GONE
        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
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
                        progressTime.text = currentPosition.toString()
                        handler.postDelayed(this, DELAY)
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        chosenTrack = (if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("chosen_Track_Key", Track::class.java)
        } else {
            intent.getParcelableExtra<Track>("chosen_Track_Key")
        })!!

        url = chosenTrack.previewUrl

        cover = findViewById(R.id.cover)
        trackName= findViewById(R.id.track_name)
        artist = findViewById(R.id.artist_name)
        progressTime = findViewById(R.id.progress)
        duration = findViewById(R.id.duration_time)
        albumName = findViewById(R.id.album_name)
        yearName = findViewById(R.id.year_name)
        genreName = findViewById(R.id.genre_name)
        countryName = findViewById(R.id.country_name)
        playButton = findViewById(R.id.play_button)
        pauseButton = findViewById(R.id.pause_button)
        val backArrow = findViewById<ImageButton>(R.id.back_button)

        preparePlayer()

        playButton.setOnClickListener {
            playbackControl()
        }

        pauseButton.setOnClickListener {
            playbackControl()
        }

        //if (chosenTrack != null) {
            bind(chosenTrack)
        //}

        backArrow.setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        mediaPlayer.release()
    }

    fun bind(item: Track){
        val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        val radius = this.resources.getDimensionPixelSize(R.dimen.dp_8)
        Glide.with(this)
            .load(item.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(radius))
            .into(cover)
        trackName.text = item.trackName
        artist.text = item.artistName
        yearName.text = item.getReleaseYear()
        genreName.text = item.primaryGenreName
        countryName.text = item.country
        duration.text = formatter.format(item.trackTimeMillis.toLong())
        albumName.text = item.collectionName
        progressTime.text = getString(R.string.time_example)
    }
}