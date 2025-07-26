package com.practicum.playlistmaker.search.ui

import android.content.Context
import android.content.SharedPreferences
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
import com.practicum.playlistmaker.settings.domain.App
import com.practicum.playlistmaker.search.ui.model.SearchState
import com.practicum.playlistmaker.util.Creator
import com.practicum.playlistmaker.util.SingleLiveEvent

class TracksViewModel(private val context: Context): ViewModel() {

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData

    private val showToast = SingleLiveEvent<String?>()
    fun observeShowToast(): LiveData<String?> = showToast

    private val removeTrack = SingleLiveEvent<Track>()
    fun observeRemoveTrack(): LiveData<Track> = removeTrack

    private val historyAdd = SingleLiveEvent<Track>()
    fun observeHistoryAdd(): LiveData<Track> = historyAdd

    private val historyRemoveAt = SingleLiveEvent<Int>()
    fun observeHistoryRemoveAt(): LiveData<Int> = historyRemoveAt

    private val getTrackListFromHistory = SingleLiveEvent<Boolean>()
    fun observeGetTrackListFromHistory(): LiveData<Boolean> = getTrackListFromHistory

    private val updateHistory = SingleLiveEvent<Boolean>()
    fun observeUpdateHistory(): LiveData<Boolean> = updateHistory

    lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener
    private var searchRunnable: Runnable? = null
    private val loadTrack = Creator.provideTracksInteractor(context)
    private val handler = Handler(Looper.getMainLooper())
    private var lastSearchText: String? = null
    private var latestSearchText: String? = null
    private val tracks = mutableListOf<Track>()
    private val getTrack = Creator.provideGetSavedTrack(context)
    private val saveTrackUseCase = Creator.provideSaveTrack(context)
    private val MAX_HISTORY_SIZE = 10

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
        updateHistory.postValue(true)
    }

    fun clearTracks(){
        tracks.clear()
    }

    fun saveTrackFromListener(track: Track){
        saveTrackUseCase.execute(track, listener)
    }

    fun doHistory(historySize: Int) {

        getTrackListFromHistory.postValue(true)

        listener = SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            val track = getTrack.execute()
            removeTrack.postValue(track!!)
            historyAdd.postValue(track)
            showToast.postValue("Сохранено")
            historySize.let {
                if (it != null) {
                    if (it > MAX_HISTORY_SIZE) {
                        historyRemoveAt.postValue(MAX_HISTORY_SIZE)
                    }
                }
            }
            updateHistory.postValue(true)
        }
    }

    override fun onCleared(){
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
        if (searchRunnable != null) {
            handler.removeCallbacks(searchRunnable!!)
            searchRunnable = null
        }
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