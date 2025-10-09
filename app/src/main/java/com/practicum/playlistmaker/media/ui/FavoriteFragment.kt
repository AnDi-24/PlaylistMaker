package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FavoriteFragmentBinding
import com.practicum.playlistmaker.media.ui.model.FavoriteStates
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.util.debounce
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class FavoriteFragment: Fragment() {

    private val viewModel by viewModel<FavoriteViewModel>()

    private var _binding: FavoriteFragmentBinding? = null
    private val binding get() = _binding!!

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private val trackAdapter = TrackAdapter {
        onTrackClickDebounce(it)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        _binding = FavoriteFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        onTrackClickDebounce = debounce<Track>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            track.isFavorite = true
            val chosenTrackToString = Json.encodeToString(track)
            findNavController().navigate(R.id.action_mediaFragment_to_playerFragment,
                PlayerFragment.createArgs(chosenTrackToString))
        }

        binding.rvTrack.adapter = trackAdapter

        viewModel.observeFavorite().observe(viewLifecycleOwner) {
            render(it)
        }
    }

    fun showContent(tracks: List<Track>) {
        binding.apply {
            placeholderMessage.visibility = View.GONE
            placeholderNotFound.visibility = View.GONE
            rvTrack.visibility = View.VISIBLE
        }
        trackAdapter.savedList.clear()
        trackAdapter.savedList.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    fun showEmpty(){
        trackAdapter.savedList.clear()
        binding.placeholderMessage.isVisible = true
        binding.placeholderNotFound.isVisible = true
        trackAdapter.notifyDataSetChanged()
    }

    fun render(state: FavoriteStates) {
        when (state) {
            is FavoriteStates.Empty -> showEmpty()
            is FavoriteStates.Content -> showContent(state.tracks)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    override fun onResume() {
        super.onResume()
        viewModel.interactor()
        trackAdapter.notifyDataSetChanged()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 100L
        fun newInstance() = FavoriteFragment().apply {
            arguments = Bundle().apply {}
        }
    }
}