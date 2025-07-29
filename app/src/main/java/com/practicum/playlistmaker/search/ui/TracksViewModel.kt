package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.search.domain.repository.HistoryChangeListener
import com.practicum.playlistmaker.search.ui.model.SearchState
import com.practicum.playlistmaker.util.Creator
import com.practicum.playlistmaker.util.SingleLiveEvent

class TracksViewModel(private val context: Context): ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val showToast = SingleLiveEvent<String?>()
    fun observeShowToast(): LiveData<String?> = showToast

    private var searchRunnable: Runnable? = null
    private val loadTrack = Creator.provideTracksInteractor(context)
    private val handler = Handler(Looper.getMainLooper())
    private var lastSearchText: String? = null
    private var latestSearchText: String? = null
    private val tracks = mutableListOf<Track>()
    private val saveTrackUseCase = Creator.provideSaveTrack(context)
    private val getListener = Creator.provideGetListener(context)
    private val unRegListener = Creator.provideUnregListenerSavedTrack(context)
    private val MAX_HISTORY_SIZE = 10

    init {
        getListener.execute(object : HistoryChangeListener {
            override fun onHistoryUpdated(track: Track?) {
                if(track != null){
                    renderState(
                        SearchState.HistoryActions(track))}
                showToast.postValue("Сохранено")
            }
        })
    }

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        searchRunnable = Runnable{val newSearchText = lastSearchText ?: ""
            requestState(newSearchText)}
        this.lastSearchText = changedText
        handler.removeCallbacks(searchRunnable!!)
        handler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
    }

    private fun renderState(state: SearchState){
        stateLiveData.postValue(state)
    }

    fun requestState(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {
            renderState(SearchState.Loading)

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
                                        SearchState.Error(
                                            context.getString(R.string.something_went_wrong),
                                            context.getDrawable(R.drawable.went_wrong),
                                            errorMessage
                                        )
                                    )
                                    showToast.postValue(errorMessage)
                                }
                                tracks.isEmpty() -> {
                                    renderState(
                                        SearchState.Empty(
                                            context.getString((R.string.nothing_found)),
                                            context.getDrawable(R.drawable.nothing)
                                        )
                                    )
                                }
                                else -> {
                                    renderState(
                                        SearchState.Content(tracks)
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    }

    fun clearButton(){
        handler.removeCallbacks(searchRunnable!!)
        renderState(
            SearchState.Content(tracks))
    }

    fun clearTracks(){
        tracks.clear()
    }

    fun saveTrackFromListener(track: Track ){
        saveTrackUseCase.execute(track)
    }

    fun cleanHistory(historySize: Int){
        if (historySize > MAX_HISTORY_SIZE){
            renderState(
                SearchState.RemoveAt(MAX_HISTORY_SIZE))}
        showToast.postValue("Сохранено")
    }

    override fun onCleared(){
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        if (searchRunnable != null) {
            handler.removeCallbacks(searchRunnable!!)
            searchRunnable = null
        }
        unRegListener.execute()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
        fun getFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    (this[ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY] as App)
                TracksViewModel(app)
            }
        }
    }
}