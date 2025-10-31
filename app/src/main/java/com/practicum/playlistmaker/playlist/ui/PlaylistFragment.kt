package com.practicum.playlistmaker.playlist.ui

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistBinding
import com.practicum.playlistmaker.media.domain.models.Playlist
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

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_COLLAPSED
        }

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


//        val metrics = resources.displayMetrics
//        val screenHeight = metrics.heightPixels
//        val peekHeightPercentage = 0.30f
//        val peekHeight = (screenHeight * peekHeightPercentage).toInt()




        binding.share.post {
            val targetRect = Rect()
            binding.share.getGlobalVisibleRect(targetRect)

            val rootRect = Rect()
            binding.root.getGlobalVisibleRect(rootRect)

            val peekHeight = targetRect.bottom - rootRect.top

            bottomSheetBehavior.peekHeight = peekHeight
        }






//        binding.share.post {
//            val targetRect = Rect()
//            binding.share.getGlobalVisibleRect(targetRect)
//
//            val windowInsets = ViewCompat.getRootWindowInsets(requireActivity().window.decorView)
//            val systemTop = windowInsets?.getInsets(WindowInsetsCompat.Type.systemBars())?.top ?: 0
//
//            val peekHeight = targetRect.bottom - systemTop
//            Log.d("значение:", "$peekHeight")
//            bottomSheetBehavior.peekHeight = peekHeight
//        }


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

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.more.setOnClickListener {
            moreBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        binding.share.setOnClickListener {
            if(!tracksOnPlaylist.isEmpty()){
                viewModel.sharePlaylist()
            }else{
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage("В этом плейлисте нет списка треков, которым можно поделиться")
                    .setNeutralButton("ОК") { dialog, which ->
                    }
                    .show()
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

    companion object{

        private const val CLICK_DEBOUNCE_DELAY = 100L

        private const val ARGS_TRACK = "playlist_id"

        fun createArgs(playlistId: Int): Bundle =
            bundleOf(ARGS_TRACK to playlistId)

    }

}
