package com.practicum.playlistmaker.presentation.tracks

import android.graphics.drawable.Drawable
import android.os.Message
import android.widget.ImageView
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.tracks.model.TracksState

interface TracksView {
    fun updateHistory()
    fun showToast(text: String)
    fun getTrackListFromHistory()
    fun historyAdd(track: Track)
    fun historyRemove(track: Track?)
    fun historySize(): Int
    fun historyRemoveAt(position: Int)


    fun render(state: TracksState)


    fun showLoading()

    fun showError(errorMessage: String, icon: Drawable?, additionalMessage: String)

    fun showEmpty(emptyMessage: String, icon: Drawable?)

    fun showContent(tracks: List<Track>)


}