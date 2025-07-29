package com.practicum.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityPlayerBinding
import com.practicum.playlistmaker.player.ui.model.PlayerStates
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.serialization.json.Json
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var chosenTrack: Track
    private lateinit var viewModel: PlayerViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        chosenTrack = Json.decodeFromString((intent.getSerializableExtra("chosen_Track_Key").toString()))

        viewModel = ViewModelProvider(
            this,
            PlayerViewModel.Companion.getFactory(chosenTrack.previewUrl)
        ).get(PlayerViewModel::class.java)


        viewModel.observePlayer().observe(this){
            if (it.playerState == PlayerStates.PLAYING){
            binding.apply {
                playButton.visibility = View.GONE
                pauseButton.visibility = View.VISIBLE
                pauseButton.isEnabled = true
            }}
        }

        viewModel.observePlayer().observe(this){
            if (it.playerState == PlayerStates.PAUSED){
            binding.apply {
                playButton.visibility = View.VISIBLE
                pauseButton.visibility = View.GONE
            }}
        }

        viewModel.observePlayer().observe(this){
            if (it.playerState == PlayerStates.PREPARED){
            binding.apply {
                playButton.isEnabled = true
                playButton.visibility = View.VISIBLE
                pauseButton.visibility = View.GONE
            }
            }
        }

        viewModel.observePlayer().observe(this){
            binding.progress.text = it.timer
        }

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.pauseButton.setOnClickListener {
            viewModel.playbackControl()
        }

        bind(chosenTrack)

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    fun bind(item: Track){
        val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())
        val radius = this.resources.getDimensionPixelSize(R.dimen.dp_8)
        Glide.with(this)
            .load(item.getCoverArtwork())
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(radius))
            .into(binding.cover)
        binding.apply {
            trackName.text = item.trackName
            artistName.text = item.artistName
            yearName.text = item.getReleaseYear()
            genreName.text = item.primaryGenreName
            countryName.text = item.country
            duration.text = formatter.format(item.trackTimeMillis.toLong())
            albumName.text = item.collectionName
            progress.text = getString(R.string.time_example)
        }
    }
}