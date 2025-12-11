package com.practicum.playlistmaker.player.ui

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.icu.text.SimpleDateFormat
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlayerBinding
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.media.ui.PlaylistBottomSheetAdapter
import com.practicum.playlistmaker.player.service.MusicService
import com.practicum.playlistmaker.player.ui.model.PlayerStates
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.util.ConnectivityChangeBroadcastReceiver
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.util.Locale
import kotlin.getValue

class PlayerFragment : Fragment() {

    private val connectivityChangeBroadcastReceiver = ConnectivityChangeBroadcastReceiver()
    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayerViewModel by viewModel()

    private val playlistBottomSheetAdapter = PlaylistBottomSheetAdapter{
        val message = viewModel.compareIds(it)
        if (message){
            Toast.makeText( context,"Добавлено в плейлист ${it.title}", Toast.LENGTH_SHORT).show()
            hideBottomSheet()
        }else{
            Toast.makeText( context,"Трек уже добавлен в плейлист ${it.title}", Toast.LENGTH_SHORT).show()
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicServiceBinder
            viewModel.setAudioPlayerControl(binder.getService())
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            viewModel.removeAudioPlayerControl()
        }
    }

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

        binding.rvPlaylist.adapter = playlistBottomSheetAdapter

        val bottomSheetContainer = binding.playlistsBottomSheet

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN}

        val overlay = binding.overlay

        val chosenTrack: Track = Json.decodeFromString(requireArguments().getSerializable("chosen_Track_Key").toString())

        val serviceTrack = requireArguments().getSerializable("chosen_Track_Key").toString()

        val intent = Intent(requireContext(), MusicService::class.java).apply {
            putExtra("track_extra", serviceTrack)
        }

        bindMusicService()

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                ContextCompat.startForegroundService(requireContext(), intent)
            } else {
                Toast.makeText(context,
                    "Can't start foreground service!",
                    Toast.LENGTH_SHORT).show()
            }
        }

        bind(chosenTrack)

        val viewModel: PlayerViewModel by viewModel { (
                parametersOf(chosenTrack)) }

        viewModel.observePlayer().observe(viewLifecycleOwner){
            render(it.playerState)
            if (it.playerState == PlayerStates.PREPARED){
                binding.apply {
                    playButton.endOfPlaying()}}
        }

        viewModel.observePlayer().observe(viewLifecycleOwner){
            likeCheck(it.isFavorite)
        }

        viewModel.observePlayer().observe(viewLifecycleOwner){
            binding.progress.text = it.timer
        }

        binding.playButton.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                ContextCompat.startForegroundService(requireContext(), intent)
            }
            viewModel.playbackControl()
        }

        binding.pauseButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.likeButton.setOnClickListener {
            viewModel.onFavoriteClicked(chosenTrack)
            if (chosenTrack.isFavorite){
                Toast.makeText(context,
                    R.string.add_to_favorite,
                    Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context,
                    R.string.remove_from_favorite,
                    Toast.LENGTH_SHORT).show()
            }
        }

        binding.addButton.setOnClickListener {
            bottomSheetBehavior.peekHeight = 1400
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        refreshBottomSheet()
                        overlay.visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        overlay.visibility = View.VISIBLE
                        refreshBottomSheet()
                    }
                    else -> {
                        overlay.visibility = View.VISIBLE

                    }
                }
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })

        binding.newPlaylist.setOnClickListener {
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }

        requireActivity().onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
        })
    }

    override fun onPause() {
        super.onPause()
        viewModel.notificationControl(true)
        requireContext().unregisterReceiver(connectivityChangeBroadcastReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        unbindMusicService()
        viewModel.notificationControl(false)
        Intent(requireContext(), MusicService::class.java).also { intent ->
            requireContext().stopService(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshBottomSheet()
        ContextCompat.registerReceiver(
            requireContext(),
            connectivityChangeBroadcastReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        viewModel.notificationControl(false)
    }

    private fun showLoading() {
        binding.apply {
            progressBar.visibility = View.VISIBLE
            playButton.visibility = View.GONE

        }
    }

    private fun showPlay() {
        binding.apply {
            progressBar.visibility = View.GONE
            playButton.visibility = View.VISIBLE
        }
    }

    private fun render(state: PlayerStates){
        when(state) {
            PlayerStates.LOADING -> showLoading()
            else ->  showPlay()
        }
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
            if(item.isFavorite){
                likeButton.setImageDrawable(getDrawable(
                    requireContext(),
                    R.drawable.like))
            }else{
                likeButton.setImageDrawable(getDrawable(
                    requireContext(),
                    R.drawable.unlike))
            }
        }
    }

    fun likeCheck(item: Boolean){
        binding.apply{
            if(item){
                likeButton.setImageDrawable(getDrawable(
                        requireContext(),
                        R.drawable.like))
            }else{
                likeButton.setImageDrawable(getDrawable(
                    requireContext(),
                    R.drawable.unlike))
            }
        }
    }

    private fun hideBottomSheet() {
        BottomSheetBehavior.from(binding.playlistsBottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    fun refreshBottomSheet(){
        viewModel.interactor()
        viewModel.dataList.observe(viewLifecycleOwner){
            playlistBottomSheetAdapter.playlists = it as MutableList<Playlist>
            binding.rvPlaylist.adapter?.notifyDataSetChanged()
        }
    }

    private fun bindMusicService() {

        val serviceTrack = requireArguments().getSerializable("chosen_Track_Key").toString()
        Intent(requireContext(), MusicService::class.java).apply {
            putExtra("track_extra", serviceTrack)
        }.also { intent ->
            requireContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

    }

    private fun unbindMusicService() {
        requireContext().unbindService(serviceConnection)
    }

    companion object{

        private const val ARGS_TRACK = "chosen_Track_Key"

        fun createArgs(chosenTrack: String): Bundle =
            bundleOf(ARGS_TRACK to chosenTrack)

    }

}