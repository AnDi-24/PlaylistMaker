package com.practicum.playlistmaker.media.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.media.ui.model.PlaylistStates
import com.practicum.playlistmaker.playlist.ui.PlaylistFragment
import com.practicum.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayListsFragment: Fragment() {

    private val playListsViewModel: PlayListsViewModel by viewModel()

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

        private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit

    val playlistAdapter = PlaylistAdapter{
        onPlaylistClickDebounce(it)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onPlaylistClickDebounce = debounce<Playlist>(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { playlist ->
            val id = playlist.id
            findNavController().navigate(R.id.action_mediaFragment_to_playlistFragment,
                PlaylistFragment.createArgs(id))
        }

        playListsViewModel.observePlaylist().observe(viewLifecycleOwner){
            render(it)
        }

        binding.newPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_mediaFragment_to_newPlaylistFragment)
        }

        playlistAdapter.onItemLongClick = { playlist->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.delete_playlist))
                .setNegativeButton(getString(R.string.no), null)
                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    playListsViewModel.deletePlaylist(playlist)
                }
                .show()
            true
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
        playlistAdapter.playlists.clear()
        playlistAdapter.playlists.addAll(playlist)
        binding.apply {
            rvPlaylist.layoutManager = GridLayoutManager(requireContext(), 2)
            rvPlaylist.adapter = playlistAdapter
            placeholderNotFound.visibility = View.GONE
            placeholderMessage.visibility = View.GONE
        }
        playlistAdapter.notifyDataSetChanged()
    }

    fun showEmpty(){
        playlistAdapter.playlists.clear()
        playlistAdapter.notifyDataSetChanged()
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
        private const val CLICK_DEBOUNCE_DELAY = 100L
        fun newInstance() = PlayListsFragment().apply{
            arguments = Bundle().apply {}
        }
    }

}