package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import androidx.core.content.edit

class TrackAdapter () : RecyclerView.Adapter<TrackViewHolder> () {

    var data = mutableListOf<Track>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {

        val trackPosition = data[position]
        holder.bind(trackPosition)
        holder.itemView.setOnClickListener {
            savedHistory.edit {
                putString(SEARCH_HISTORY_KEY, createJsonFromTrack(trackPosition))
                savedHistory.registerOnSharedPreferenceChangeListener(listener)
            }}
    }

    private fun createJsonFromTrack(track: Track): String {
        return Gson().toJson(track)
    }

    override fun getItemCount(): Int {
        return data.size
    }
}


