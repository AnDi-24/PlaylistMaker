package com.practicum.playlistmaker


import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class TrackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val albumIcon: ImageView = itemView.findViewById(R.id.albumIcon)
    private val track: TextView = itemView.findViewById(R.id.track)
    private val artist: TextView = itemView.findViewById(R.id.artist)
    private val duration: TextView = itemView.findViewById(R.id.duration)
    private val radius = itemView.context.resources.getDimensionPixelSize(R.dimen.album_corners)

    fun bind(item: Track){
        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(radius))
            .into(albumIcon)
        track.text = item.trackName
        artist.text = item.artistName
        duration.text = item.trackTime
    }
}



