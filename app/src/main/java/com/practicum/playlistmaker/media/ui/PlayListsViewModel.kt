package com.practicum.playlistmaker.media.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.media.ui.model.PlaylistStates
import kotlinx.coroutines.launch

class PlayListsViewModel(
    private val playlistInteractor: PlaylistInteractor,
): ViewModel()  {

    private val playlistLiveData = MutableLiveData<PlaylistStates>()
    fun observePlaylist(): LiveData<PlaylistStates> = playlistLiveData

    init {
        interactor()
    }

    private fun processResult(playlist: List<Playlist>) {

        when{
            playlist.isEmpty() -> {
                renderState(
                    PlaylistStates.Empty
                )
            }else -> {
            renderState(
                PlaylistStates.Content(playlist)
            )}
        }
    }

    private fun renderState(state: PlaylistStates) {
        playlistLiveData.postValue(state)
    }


    fun interactor(){
        viewModelScope.launch {
            playlistInteractor
                .getAllPlaylists()
                .collect { playlists ->
                    processResult(playlists)
                }
        }
    }

}