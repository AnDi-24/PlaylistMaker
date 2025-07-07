package com.practicum.playlistmaker.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track

class TrackAdapter (private val onTrackClick: (Track) -> Unit
) : RecyclerView.Adapter<TrackViewHolder> () {

    var savedList = mutableListOf<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        val trackPosition = savedList[position]
        holder.bind(trackPosition)
        holder.itemView.setOnClickListener {
            onTrackClick.invoke(trackPosition)
            }
    }

    override fun getItemCount(): Int {
        return savedList.size
    }
}


