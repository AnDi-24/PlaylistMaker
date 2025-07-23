package com.practicum.playlistmaker.presentation.tracks


import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.tracks.model.TracksState
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType

interface TracksView: MvpView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun render(state: TracksState)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showToast(text: String)

    fun updateHistory()

    fun getTrackListFromHistory()

    fun historyAdd(track: Track)

    fun historyRemove(track: Track?)

    fun historySize(): Int

    fun historyRemoveAt(position: Int)





//    fun showLoading()
//
//    fun showError(errorMessage: String, icon: Drawable?, additionalMessage: String)
//
//    fun showEmpty(emptyMessage: String, icon: Drawable?)
//
//    fun showContent(tracks: List<Track>)


}