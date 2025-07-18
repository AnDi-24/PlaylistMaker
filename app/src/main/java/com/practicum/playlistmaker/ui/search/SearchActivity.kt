package com.practicum.playlistmaker.ui.search

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.util.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.use_case.GetTrackListUseCase
import com.practicum.playlistmaker.domain.use_case.SaveHistoryUseCase
import com.practicum.playlistmaker.presentation.tracks.TracksView
import com.practicum.playlistmaker.ui.player.PlayerActivity
import com.practicum.playlistmaker.ui.tracks.model.TracksState

class SearchActivity : AppCompatActivity(), TracksView {

    private var isClickAllowed = true

    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderNotFound: ImageView
    private lateinit var refreshButton: ImageView
    private lateinit var inputEditText: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var rvTrack: RecyclerView
    private lateinit var historyView: LinearLayout
    private var textWatcher: TextWatcher? = null

    private val handler = Handler(Looper.getMainLooper())

    private val trackAdapter = TrackAdapter {
        val chosenTrack: Track = it
        if (clickDebounce()) {
            startActivity(playerIntent.putExtra("chosen_Track_Key", chosenTrack))
            tracksSearchPresenter.saveTrackFromListener(it)
        }
    }

    private val historyAdapter = TrackAdapter {
        val chosenTrack: Track = it
        startActivity(playerIntent.putExtra("chosen_Track_Key", chosenTrack))
    }

    private lateinit var loadTrack: TracksInteractor
    private lateinit var getTrackListUseCase: GetTrackListUseCase
    private lateinit var saveHistoryUseCase: SaveHistoryUseCase
    private var inputValue: String = INPUT_DEF
    lateinit var playerIntent: Intent

    private val tracksSearchPresenter =
        Creator.provideTracksSearchPresenter(this, this)

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_STATE, inputValue)
    }


    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputValue = savedInstanceState.getString(
            INPUT_STATE,
            INPUT_DEF
        )
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

//        tracksSearchPresenter = (this.applicationContext as? App)?.tracksSearchPresenter
//
//        if (tracksSearchPresenter == null) {
//            tracksSearchPresenter = Creator.provideTracksSearchPresenter(this, this)
//            (this.applicationContext as? SearchActivity)?.tracksSearchPresenter = tracksSearchPresenter
//
//        }

        inputEditText = findViewById(R.id.inputEditText)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderNotFound = findViewById(R.id.placeholderNotFound)
        refreshButton = findViewById(R.id.refreshButton)
        progressBar = findViewById(R.id.progressBar)
        rvTrack = findViewById(R.id.rvTrack)
        historyView = findViewById(R.id.historyView)
        val clearButton = findViewById<ImageView>(R.id.clear_icon)
        val clearHistoryButton = findViewById<ImageView>(R.id.clearHistoryButton)
        val foundTrack = findViewById<RecyclerView>(R.id.foundTrack)
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager

        loadTrack = Creator.provideTracksInteractor(this)
        getTrackListUseCase = Creator.provideGetTrackList(this)
        saveHistoryUseCase = Creator.provideSaveHistory(this)
        playerIntent = Intent(this, PlayerActivity::class.java)

        val backButton = findViewById<ImageView>(R.id.button_back)

        tracksSearchPresenter.doHistory()

        backButton.setOnClickListener {
            finish()
        }

        rvTrack.adapter = trackAdapter
        foundTrack.adapter = historyAdapter
        inputEditText.setText(inputValue)


        refreshButton.setOnClickListener {
            tracksSearchPresenter.requestState(inputEditText.toString())
            refreshButton.visibility = View.GONE
        }

        clearHistoryButton.setOnClickListener {
            historyAdapter.savedList.clear()
            historyAdapter.notifyDataSetChanged()
            historyView.visibility = View.GONE
        }

        clearHistoryButton.setOnClickListener {
            historyAdapter.savedList.clear()
            historyAdapter.notifyDataSetChanged()
            historyView.visibility = View.GONE
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            tracksSearchPresenter.clearButton()
            inputMethodManager?.hideSoftInputFromWindow(clearButton.windowToken, 0)
        }

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            historyView.visibility =
                if (hasFocus && inputEditText.text.isEmpty() && !historyAdapter.savedList.isEmpty()) View.VISIBLE else View.GONE
        }

        inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                tracksSearchPresenter.searchDebounce(changedText = s?.toString() ?: "")

                historyView.visibility =
                    if (inputEditText.hasFocus() && s?.isEmpty() == true) View.VISIBLE else View.GONE

                rvTrack.visibility = if (inputEditText.text.isEmpty()) View.GONE else View.VISIBLE

                if (inputEditText.text.isEmpty()) {
                    tracksSearchPresenter.clearTracks()
                }

                clearButton.visibility = clearButtonVisibility(s)

                val input = s.toString()
                inputValue = input
            }

            override fun afterTextChanged(s: Editable?) {
                placeholderMessage.visibility = View.GONE
                placeholderNotFound.visibility = View.GONE
                refreshButton.visibility = View.GONE

            }
        })

        textWatcher?.let { inputEditText.addTextChangedListener(it) }

        tracksSearchPresenter.onCreate()

    }

    override fun onStop() {
        super.onStop()
        saveHistoryUseCase.execute(historyAdapter.savedList)
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { inputEditText.removeTextChangedListener(it) }

        tracksSearchPresenter.onDestroy()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun updateHistory() {
        historyAdapter.notifyDataSetChanged()
    }

    override fun showToast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT)
            .show()
    }

    override fun getTrackListFromHistory() {
        historyAdapter.savedList =
            getTrackListUseCase.execute() ?: emptyList<Track>().toMutableList()
    }

    override fun historyAdd(track: Track) {
        historyAdapter.savedList.add(0, track)
    }

    override fun historyRemove(track: Track?) {
        historyAdapter.savedList.remove(track)
    }

    override fun historySize(): Int {
        return historyAdapter.savedList.size
    }

    override fun historyRemoveAt(position: Int) {
        historyAdapter.savedList.removeAt(position)
    }

    override fun showContent(tracks: List<Track>) {
        progressBar.visibility = View.GONE
        placeholderMessage.visibility = View.GONE
        placeholderNotFound.visibility = View.GONE
        refreshButton.visibility = View.GONE
        rvTrack.visibility = View.VISIBLE
        trackAdapter.savedList.clear()
        trackAdapter.savedList.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    override fun showLoading() {
        placeholderMessage.visibility = View.GONE
        placeholderNotFound.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        historyView.visibility = View.GONE
        rvTrack.visibility = View.GONE
    }

    override fun showEmpty(emptyMessage: String, icon: Drawable?) {
        placeholderMessage.visibility = View.VISIBLE
        placeholderNotFound.visibility = View.VISIBLE
        tracksSearchPresenter.clearTracks()
        trackAdapter.savedList.clear()
        trackAdapter.notifyDataSetChanged()
        placeholderMessage.text = emptyMessage
        placeholderNotFound.setImageDrawable(icon)
    }

    override fun showError(errorMessage: String, icon: Drawable?, additionalMessage: String) {
        placeholderMessage.visibility = View.VISIBLE
        placeholderNotFound.visibility = View.VISIBLE
        tracksSearchPresenter.clearTracks()
        trackAdapter.savedList.clear()
        trackAdapter.notifyDataSetChanged()
        placeholderMessage.text = errorMessage
        placeholderNotFound.setImageDrawable(icon)
        Toast.makeText(this, additionalMessage, Toast.LENGTH_SHORT)
            .show()
        refreshButton.visibility = View.VISIBLE
        refreshButton.setImageDrawable(getDrawable(R.drawable.refresh_button))
    }

    override fun render(state: TracksState) {
        when(state) {
            is TracksState.Loading -> showLoading()
            is TracksState.Error -> showError(state.errorMessage, state.icon, state.additionalMessage)
            is TracksState.Empty -> showEmpty(state.emptyMessage, state.icon)
            is TracksState.Content ->showContent(state.tracks)
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val INPUT_STATE = "INPUT_STATE"
        private const val INPUT_DEF = ""
    }
}