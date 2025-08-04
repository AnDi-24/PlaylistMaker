package com.practicum.playlistmaker.search.ui


import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.RecyclerViewBinding
import com.practicum.playlistmaker.search.domain.models.Track
import java.util.Locale

class TrackViewHolder(private val binding: RecyclerViewBinding): RecyclerView.ViewHolder(binding.root) {

    private val radius = itemView.context.resources.getDimensionPixelSize(R.dimen.dp_2)
    private val formatter = SimpleDateFormat("mm:ss", Locale.getDefault())

    fun bind(item: Track){
        Glide.with(itemView)
            .load(item.artworkUrl100)
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(radius))
            .into(binding.albumIcon)
        binding.apply {
            track.text = item.trackName
            artist.text = item.artistName
            duration.text = formatter.format(item.trackTimeMillis.toLong())
        }
    }

    companion object{
        fun from(parent: ViewGroup): TrackViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = RecyclerViewBinding.inflate(inflater, parent, false)
            return TrackViewHolder(binding)
        }
    }
}



