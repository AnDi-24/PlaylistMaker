package com.practicum.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.player.ui.model.PlayerStates
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.Locale
import kotlin.getValue

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PlayerViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chosenTrack: Track = Json.decodeFromString(requireArguments().getSerializable("chosen_Track_Key").toString())

        val viewModel: PlayerViewModel by viewModel { (
                parametersOf(chosenTrack.previewUrl)) }

        viewModel.observePlayer().observe(viewLifecycleOwner){
            if (it.playerState == PlayerStates.PLAYING){
                binding.apply {
                    playButton.isVisible = false
                    pauseButton.isVisible = true
                    pauseButton.isEnabled = true
                }
            }
        }

        viewModel.observePlayer().observe(viewLifecycleOwner){
            if (it.playerState == PlayerStates.PAUSED){
                binding.apply {
                    playButton.isVisible = true
                    pauseButton.isVisible = false
                }}
        }

        viewModel.observePlayer().observe(viewLifecycleOwner){
            if (it.playerState == PlayerStates.PREPARED){
                binding.apply {
                    playButton.isEnabled = true
                    playButton.isVisible = true
                    pauseButton.isVisible = false
                }
            }
        }

        viewModel.observePlayer().observe(viewLifecycleOwner){
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
            findNavController().navigateUp()
        }
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
            durationTime.text = formatter.format(item.trackTimeMillis.toLong())
            albumName.text = item.collectionName
            progress.text = getString(R.string.time_example)
        }
    }

    companion object{

        private const val ARGS_TRACK = "chosen_Track_Key"

        fun createArgs(chosenTrack: String): Bundle =
            bundleOf(ARGS_TRACK to chosenTrack)

    }

}