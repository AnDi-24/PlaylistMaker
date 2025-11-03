package com.practicum.playlistmaker.media.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.media.domain.models.Playlist

class PlaylistBottomSheetAdapter (
    private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistBottomSheetViewHolder> () {

    var playlists = mutableListOf<Playlist>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistBottomSheetViewHolder =
        PlaylistBottomSheetViewHolder.from(parent)

    override fun onBindViewHolder(holder: PlaylistBottomSheetViewHolder, position: Int) {
        val playlistPosition = playlists[position]
        holder.bind(playlistPosition)
        holder.itemView.setOnClickListener {
            onPlaylistClick.invoke(playlistPosition)
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

}