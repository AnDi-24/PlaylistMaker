package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.media.ui.model.PlaylistStates
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayListsFragment: Fragment() {

    private val playListsViewModel: PlayListsViewModel by viewModel()

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playListsViewModel.observePlaylist().observe(viewLifecycleOwner){
            render(it)
        }

        binding.newPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_newPlaylistFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        playListsViewModel.interactor()
        playListsViewModel.observePlaylist().observe(viewLifecycleOwner){
            render(it)
        }
    }

    fun showContent(playlist: List<Playlist>) {
        binding.apply {
            rvPlaylist.layoutManager = GridLayoutManager(requireContext(), 2)
            rvPlaylist.adapter = PlaylistAdapter(playlist)
            placeholderNotFound.visibility = View.GONE
            placeholderMessage.visibility = View.GONE
        }
    }

    fun showEmpty(){
        binding.apply {
            placeholderNotFound.visibility = View.VISIBLE
            placeholderMessage.visibility = View.VISIBLE
        }
    }

    fun render(state: PlaylistStates) {
        when (state) {
            is PlaylistStates.Empty -> showEmpty()
            is PlaylistStates.Content -> showContent(state.playlist)
        }
    }

    companion object {
        fun newInstance() = PlayListsFragment().apply{}

    }
}