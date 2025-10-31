package com.practicum.playlistmaker.playlist.ui


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.media.domain.db.PlaylistInteractor
import com.practicum.playlistmaker.media.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.sharing.domain.api.SharingInteractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.collections.mutableListOf

class PlaylistViewModel(
    private val playlistInteractor: PlaylistInteractor,
    private val sharingInteractor: SharingInteractor
): ViewModel() {

    private val liveData = MutableLiveData<Playlist>()
    var playlist: LiveData<Playlist> = liveData

    var tracksIds: MutableList<String> =  mutableListOf()

    val gson = Gson()

    lateinit var currentPlaylist: Playlist
    private val _tracks = MutableStateFlow<List<Track>>(emptyList())
    val tracks: StateFlow<List<Track>> = _tracks

    fun interactor(id: Int){
        viewModelScope.launch {
            playlistInteractor
                .getPlaylistById(id)
                .collect {
                    currentPlaylist = it
                    liveData.value = it
                    tracksOnPlaylist()
        }
    }}

    fun getTracks(): List<Track>{
        val tracks = _tracks.value
        return tracks
    }

    fun deleteTrack(track: Track){

        viewModelScope.launch {
            tracksIds.clear()
            tracksIds.addAll(gson.fromJson(
                currentPlaylist.trackIds,
                object : TypeToken<MutableList<String>>() {}.type
            ))
            tracksIds.removeIf { it == track.trackId }

            val updatedPlaylist = Playlist(
                currentPlaylist.id,
                currentPlaylist.title,
                currentPlaylist.description,
                currentPlaylist.coverImagePath,
                gson.toJson(tracksIds),
                tracksIds.size
            )

            playlistInteractor.updatePlaylist(updatedPlaylist, track.trackId)

            currentPlaylist = updatedPlaylist

            tracksOnPlaylist()
        }
    }

    fun durationInTotal(): String{
        val tracks = _tracks.value
        val total = tracks.sumOf { it.trackTimeMillis.toInt() }
        val totalMinutes = (total / 1000) / 60
        return when {
            totalMinutes % 100 in 11..14 -> "$totalMinutes минут"
            totalMinutes % 10 == 1 -> "$totalMinutes минута"
            totalMinutes % 10 in 2..4 -> "$totalMinutes минуты"
            else -> "$totalMinutes минут"
        }
    }

    fun getTrackCountString(): String {
        return when {
            currentPlaylist.tracksCount % 100 in 11..14 -> "${currentPlaylist.tracksCount} треков"
            currentPlaylist.tracksCount % 10 == 1 -> "${currentPlaylist.tracksCount} трек"
            currentPlaylist.tracksCount % 10 in 2..4 -> "${currentPlaylist.tracksCount} трека"
            else -> "${currentPlaylist.tracksCount} треков"
        }
    }

    fun sharePlaylist(){
        val tracksToShare = sharingInteractor.getTracksToShare(getTracks())
        val content = "${currentPlaylist.title}\n${currentPlaylist.description}\n${getTrackCountString()}\n${tracksToShare.joinToString(separator = "\n"){"${it.title} (${it.track}) ${it.duration}"}}"
        sharingInteractor.share(content)
    }

    fun tracksOnPlaylist() {
        val trackIds = gson.fromJson<List<String>>(
            currentPlaylist.trackIds,
            object : TypeToken<List<String>>() {}.type
        )
        viewModelScope.launch {
            playlistInteractor
                .getTracks(trackIds)
                .collect { tracksList ->
                    _tracks.value = tracksList
                }
        }
    }
}