package com.practicum.playlistmaker.search.ui

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.repository.HistoryChangeListener
import com.practicum.playlistmaker.search.domain.use_case.GetListenerUseCase
import com.practicum.playlistmaker.search.domain.use_case.GetTrackListUseCase
import com.practicum.playlistmaker.search.domain.use_case.SaveHistoryUseCase
import com.practicum.playlistmaker.search.domain.use_case.SaveTrackUseCase
import com.practicum.playlistmaker.search.domain.use_case.UnregListenerSavedTrack
import com.practicum.playlistmaker.search.ui.model.SearchState
import com.practicum.playlistmaker.util.SingleLiveEvent

class SearchViewModel(
    private val loadTrack: TracksInteractor,
    private val saveTrackUseCase: SaveTrackUseCase,
    private val getListener: GetListenerUseCase,
    private val unRegListener: UnregListenerSavedTrack,
    private val saveHistory: SaveHistoryUseCase,
    private val getTrackList: GetTrackListUseCase
    ): ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val showToast = SingleLiveEvent<String?>()
    fun observeShowToast(): LiveData<String?> = showToast

    private var searchRunnable: Runnable? = null
    private val handler = Handler(Looper.getMainLooper())
    private var lastSearchText: String? = null
    private var latestSearchText: String? = null
    private val tracks = mutableListOf<Track>()
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
                                            errorMessage
                                        )
                                    )
                                    showToast.postValue(errorMessage)
                                }
                                tracks.isEmpty() -> {
                                    renderState(
                                        SearchState.Empty
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

    fun saveHistoryFromAdapter(historyFromAdapter: MutableList<Track>){
        saveHistory.execute(historyFromAdapter)
    }

    fun getTrackListFromPref(): MutableList<Track>?{
        return getTrackList.execute()
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
    }
}