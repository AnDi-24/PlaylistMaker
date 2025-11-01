package com.practicum.playlistmaker.media.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.media.domain.models.Playlist

class PlaylistAdapter (private val onPlaylistClick: (Playlist) -> Unit
) : RecyclerView.Adapter<PlaylistViewHolder> () {

    var playlists = mutableListOf<Playlist>()

    var onItemLongClick: ((Playlist) -> Boolean)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder =
        PlaylistViewHolder.from(parent)


    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlistPosition = playlists[position]
        holder.bind(playlistPosition)
        holder.itemView.setOnClickListener {
            onPlaylistClick.invoke(playlistPosition)
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClick?.invoke(playlistPosition) ?: false
        }
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

}