package com.practicum.playlistmaker.player.ui

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.player.service.AudioPlayerControl
import com.practicum.playlistmaker.player.ui.model.PlayerData
import com.practicum.playlistmaker.player.ui.model.PlayerStates
import com.practicum.playlistmaker.search.domain.db.FavoriteInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.launch
import java.util.Locale

class PlayerViewModel(private val track: Track,
                      private val mediaPlayer: MediaPlayer,
                      private val playlistInteractor: PlaylistInteractor,
                      private val favoritesInteractor: FavoriteInteractor)
    : ViewModel() {

    private var playerState = PlayerStates.DEFAULT
    private var audioPlayerControl: AudioPlayerControl? = null
    private var timer = "00:00"
    private val playerLiveData = MutableLiveData(PlayerData(PlayerStates.DEFAULT, timer, track.isFavorite, true))
    fun observePlayer(): LiveData<PlayerData> = playerLiveData
    private val bottomSheetLiveData = MutableLiveData<List<Playlist>>()
    var dataList: LiveData<List<Playlist>> = bottomSheetLiveData
    val gson = Gson()

     var tracksIds: MutableList<String> =  mutableListOf()

    init {
        interactor()
    }

    override fun onCleared() {
        super.onCleared()
        audioPlayerControl = null
    }

    fun setAudioPlayerControl(audioPlayerControl: AudioPlayerControl) {
        this.audioPlayerControl = audioPlayerControl

        viewModelScope.launch {
            audioPlayerControl.getData().collect {
                playerLiveData.postValue(it)
                playerState = it.playerState
            }
        }
    }

    fun notificationControl(show: Boolean){
        if (show){
            audioPlayerControl?.showForegroundService()
        }else{
            audioPlayerControl?.hideForegroundService()
        }
    }

    fun playbackControl() {
        when(playerState) {
            PlayerStates.PLAYING -> {
                audioPlayerControl?.pausePlayer()
            }PlayerStates.PAUSED, PlayerStates.PREPARED -> {
                audioPlayerControl?.startPlayer()
            }else -> { }
        }
    }

    fun removeAudioPlayerControl() {
        audioPlayerControl = null
    }

    private fun getCurrentPlayerPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition) ?: "00:00"
    }

    fun onFavoriteClicked(track: Track){
        viewModelScope.launch {
            if (!track.isFavorite) {
                favoritesInteractor.addTrack(track)
                track.isFavorite = true
                playerLiveData.postValue(PlayerData(playerState, getCurrentPlayerPosition(),
                    isFavorite = true,
                    isLoading = false
                ))
            }else{
                favoritesInteractor.removeTrack(track)
                track.isFavorite = false
                playerLiveData.postValue(PlayerData(playerState, getCurrentPlayerPosition(),
                    isFavorite = false,
                    isLoading = false
                ))
            }
        }
    }

    fun compareIds(playlist: Playlist): Boolean{

        tracksIds.clear()

        tracksIds.addAll(gson.fromJson(playlist.trackIds, object : TypeToken<MutableList<String>>() {}.type))

        var fragmentReact = false
        if (tracksIds.isEmpty()) {
            addTrackToPlaylist(playlist)
            fragmentReact = true
        } else {
            if (!tracksIds.contains(track.trackId)) {
                addTrackToPlaylist(playlist)
                fragmentReact = true
            }
        }
        return fragmentReact
    }

    fun interactor(){
        viewModelScope.launch {
            playlistInteractor
                .getAllPlaylists()
                .collect { playlists ->
                    bottomSheetLiveData.value = playlists
                }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist){
        playlistInteractor.saveTrackToPlaylist(track)
        interactor()
        tracksIds.add(track.trackId)
        val updatedPlaylist = Playlist(
            playlist.id,
            playlist.title,
            playlist.description,
            playlist.coverImagePath,
            gson.toJson(tracksIds),
            tracksIds.size
        )
        playlistInteractor.updatePlaylist(updatedPlaylist, track.trackId)
    }
}