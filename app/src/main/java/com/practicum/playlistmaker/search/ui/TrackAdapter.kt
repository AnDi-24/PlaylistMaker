package com.practicum.playlistmaker.search.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.search.domain.models.Track

class TrackAdapter (private val onTrackClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder> () {

    var savedList = mutableListOf<Track>()
    var onItemLongClick: ((Track) -> Boolean)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder =
        TrackViewHolder.from(parent)


    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val trackPosition = savedList[position]
        holder.bind(trackPosition)
        holder.itemView.setOnClickListener {
            onTrackClick.invoke(trackPosition)
            }
        holder.itemView.setOnLongClickListener {
            onItemLongClick?.invoke(trackPosition) ?: false
        }
    }

    override fun getItemCount(): Int {
        return savedList.size
    }


}


