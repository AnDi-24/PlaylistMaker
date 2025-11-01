package com.practicum.playlistmaker.playlist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.media.ui.EditPlaylistFragment
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.TrackAdapter
import com.practicum.playlistmaker.util.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class PlaylistFragment: Fragment() {

    private val viewModel: PlaylistViewModel by viewModel()

    private var _binding: FragmentPlaylistBinding? = null
    private val binding get() = _binding!!
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    lateinit var tracksOnPlaylist: List<Track>

    lateinit var currentPlaylist: Playlist

    private val trackAdapter = TrackAdapter {
        onTrackClickDebounce(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val id = requireArguments().getInt("playlist_id")

        viewModel.interactor(id)

        val bottomSheetContainer = binding.playlistsBottomSheet

        val moreBottomSheetContainer = binding.moreBottomSheet

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer)

        val moreBottomSheetBehavior = BottomSheetBehavior.from(moreBottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        moreBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        binding.overlay.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.overlay.visibility = View.VISIBLE

                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.more.post {
            val rootHeight = binding.root.height
            val moreBottom = binding.more.bottom
            val offset = (24 * resources.displayMetrics.density).toInt()

            val desiredPeekHeight = rootHeight - moreBottom - offset
            bottomSheetBehavior.peekHeight = desiredPeekHeight
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }


        viewModel.playlist.observe(viewLifecycleOwner) {
            bind(it)
        }

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            val chosenTrackToString = Json.encodeToString(track)
            findNavController().navigate(
                R.id.action_playlistFragment_to_playerFragment,
                PlayerFragment.createArgs(chosenTrackToString)
            )
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigateUp()
            }
        })

        binding.apply {
            backButton.setOnClickListener {
                findNavController().navigateUp()
            }
            more.setOnClickListener {
                moreBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            share.setOnClickListener {
                if (share()){
                    moreBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
            moreShare.setOnClickListener {
                if (share()){
                    moreBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
            moreDelete.setOnClickListener {
                moreBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                delete()
            }
            moreEdit.setOnClickListener {
                findNavController().navigate(R.id.action_playlistFragment_to_editPlaylistFragment,
                    EditPlaylistFragment.createArgs(currentPlaylist.title,
                        currentPlaylist.description,
                        currentPlaylist.coverImagePath,
                        currentPlaylist.id))
            }

        }

        trackAdapter.onItemLongClick = { track ->
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Хотите удалить трек?")
                .setNegativeButton("Нет", null)
                .setPositiveButton("Да") { _, _ ->
                    viewModel.deleteTrack(track)
                }
                .show()
            true
        }
        binding.rvTrack.adapter = trackAdapter
        trackAdapter.notifyDataSetChanged()
    }

    private fun bind(playlist: Playlist){
        currentPlaylist = playlist
        Glide.with(this)
            .load(playlist.coverImagePath)
            .placeholder(R.drawable.placeholder)
            .into(binding.cover)
        Glide.with(this)
            .load(playlist.coverImagePath)
            .placeholder(R.drawable.placeholder)
            .into(binding.miniCover)
        binding.apply {
            title.text = playlist.title
            miniTitle.text = playlist.title
            description.text = playlist.description
        }
        viewModel.tracks.onEach { tracks ->
            tracksOnPlaylist = tracks
            binding.generalDuration.text = viewModel.durationInTotal()
            binding.tracksInTotal.text = viewModel.getTrackCountString()
            binding.sum.text = viewModel.getTrackCountString()
            trackAdapter.savedList.clear()
            trackAdapter.savedList.addAll(tracks)
            trackAdapter.notifyDataSetChanged()
        }.launchIn(lifecycleScope)
    }

    private fun share(): Boolean{
        if(!tracksOnPlaylist.isEmpty()){
            viewModel.sharePlaylist()
            return false
        }else{
            MaterialAlertDialogBuilder(requireContext())
                .setMessage("В этом плейлисте нет списка треков, которым можно поделиться")
                .setNeutralButton("ОК") { dialog, which ->
                }
                .show()
        }
        return true
    }

    private fun delete(){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Хотите удалить плейлист?")
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deletePlaylist()
                findNavController().navigateUp()
            }
            .show()
        true
    }

    companion object{

        private const val CLICK_DEBOUNCE_DELAY = 100L

        private const val ARGS_TRACK = "playlist_id"

        fun createArgs(playlistId: Int): Bundle =
            bundleOf(ARGS_TRACK to playlistId)
    }

}
