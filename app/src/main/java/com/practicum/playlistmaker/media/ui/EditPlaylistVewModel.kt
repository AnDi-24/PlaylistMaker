package com.practicum.playlistmaker.media.ui

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.models.Playlist
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EditPlaylistViewModel(playlistInteractor: PlaylistInteractor,
                            context: Context
) : NewPlaylistViewModel(playlistInteractor, context){
    lateinit var playlist: Playlist

    fun saveChanges(playlistTitle: String,
                    playlistDescription: String,
                    playlistCover: String,
                    playlistId: Int){
        viewModelScope.launch {
            playlistInteractor
                .getPlaylistById(playlistId)
                .collect {
                    playlist = (
                            Playlist(
                                it.id,
                                playlistTitle,
                                playlistDescription,
                                playlistCover,
                                it.trackIds,
                                it.tracksCount))
                }
            delay(200)
            playlistInteractor.updatePlaylist(playlist, "")

        }

    }

}