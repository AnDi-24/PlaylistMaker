package com.practicum.playlistmaker.presentation.tracks

import android.content.Context
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.use_case.GetSavedTrackUseCase
import com.practicum.playlistmaker.domain.use_case.SaveTrackUseCase
import com.practicum.playlistmaker.ui.search.TrackAdapter
import com.practicum.playlistmaker.ui.tracks.model.TracksState
import com.practicum.playlistmaker.util.Creator
import moxy.MvpPresenter


class TracksSearchPresenter(
    private val context: Context
): MvpPresenter<TracksView>() {

//    private var view: TracksView? = null
//    private var state: TracksState? = null
    private var latestSearchText: String? = null


    private val MAX_HISTORY_SIZE = 10
    private var searchRunnable: Runnable? = null
    private val loadTrack = Creator.provideTracksInteractor(context)
    lateinit var listener: OnSharedPreferenceChangeListener
    private lateinit var getTrack: GetSavedTrackUseCase
    private lateinit var saveTrackUseCase: SaveTrackUseCase



    private var lastSearchText: String? = null
    private val tracks = mutableListOf<Track>()
    private val handler = Handler(Looper.getMainLooper())

//    fun attachView(view: TracksView) {
//        this.view = view
//        state?.let { view.render(it)}
//    }
//
//    fun detachView() {
//        this.view = null
//    }

    private fun renderState(state: TracksState) {
        viewState.render(state)
//        this.state = state
//        this.view?.render(state)
    }

    fun onCreate(){

        searchRunnable = Runnable{val newSearchText = lastSearchText ?: ""
            requestState(newSearchText)}

        getTrack = Creator.provideGetSavedTrack(context)
        saveTrackUseCase = Creator.provideSaveTrack(context)

    }

    override fun onDestroy(){
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
//        if (searchRunnable != null) {
//            handler.removeCallbacks(searchRunnable!!)
//            searchRunnable = null
//        }
    }

    fun requestState(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(TracksState.Loading)

            loadTrack.search(
                newSearchText,
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?, errorMessage: String?){
                        handler.post {
                            if (foundTracks != null){
                                tracks.clear()
                                tracks.addAll(foundTracks)
                            }

                            when {
                                errorMessage != null -> {
                                    renderState(
                                        TracksState.Error(
                                            context.getString(R.string.something_went_wrong),
                                            context.getDrawable(R.drawable.went_wrong),
                                            errorMessage
                                        )
                                    )
                                }
                                tracks.isEmpty() -> {
                                    renderState(
                                        TracksState.Empty(
                                        context.getString((R.string.nothing_found)),
                                        context.getDrawable(R.drawable.nothing)
                                        )

                                    )
                                }
                                else -> {
                                    renderState(
                                        TracksState.Content(tracks)
                                    )
                                }

                            }
                        }
                    }
                }
            )
        }
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        this.lastSearchText = changedText
        handler.removeCallbacks(searchRunnable!!)
        handler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
    }

    fun clearButton(){
        handler.removeCallbacks(searchRunnable!!)
        renderState(
            TracksState.Content(tracks))
        viewState.updateHistory()

    }

    fun clearTracks(){
        tracks.clear()
    }

    private fun removeTrack(track: Track?) {
        viewState.historyRemove(track)
    }

    fun saveTrackFromListener(track: Track){
        saveTrackUseCase.execute(track, listener)
    }

    fun doHistory() {

        viewState.getTrackListFromHistory()

        listener = OnSharedPreferenceChangeListener { sharedPreferences, key ->
            val track = getTrack.execute()
            removeTrack(track)
            viewState.historyAdd(track!!)
            viewState.showToast("Сохранено")
            viewState.historySize().let {
                if (it > MAX_HISTORY_SIZE) {
                    viewState.historyRemoveAt(MAX_HISTORY_SIZE)
                }
            }
            viewState.updateHistory()
        }
    }
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()

    }
}