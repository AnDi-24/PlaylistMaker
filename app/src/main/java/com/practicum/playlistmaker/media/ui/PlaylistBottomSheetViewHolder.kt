package com.practicum.playlistmaker.media.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.RecyclerViewPlaylistsBottomsheetBinding
import com.practicum.playlistmaker.media.domain.models.Playlist

class PlaylistBottomSheetViewHolder (
    private val binding: RecyclerViewPlaylistsBottomsheetBinding
): RecyclerView.ViewHolder(binding.root) {

    private val radius = itemView.context.resources.getDimensionPixelSize(R.dimen.dp_8)

    fun bind (item: Playlist){
        Glide.with(itemView)
            .load(item.coverImagePath)
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(radius))
            .into(binding.cover)
        binding.apply {
            title.text = item.title
            sum.text = getTrackCountString(item.tracksCount)
        }
    }

    fun getTrackCountString(count: Int): String {
        return when {
            count % 100 in 11..14 -> "$count треков"
            count % 10 == 1 -> "$count трек"
            count % 10 in 2..4 -> "$count трека"
            else -> "$count треков"
        }
    }

    companion object{
        fun from(parent: ViewGroup): PlaylistBottomSheetViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = RecyclerViewPlaylistsBottomsheetBinding.inflate(inflater, parent, false)
            return PlaylistBottomSheetViewHolder(binding)
        }
    }

}