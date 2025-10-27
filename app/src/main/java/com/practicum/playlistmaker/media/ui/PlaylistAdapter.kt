package com.practicum.playlistmaker.media.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.media.domain.models.Playlist

class PlaylistAdapter (private var playlists: List<Playlist>
) : RecyclerView.Adapter<PlaylistViewHolder> () {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder =
        PlaylistViewHolder.from(parent)


    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

}