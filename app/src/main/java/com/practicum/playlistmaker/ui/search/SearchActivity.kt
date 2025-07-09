package com.practicum.playlistmaker.ui.search

import android.content.Intent
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
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
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.domain.api.TracksInteractor
import com.practicum.playlistmaker.domain.use_case.GetSavedTrackUseCase
import com.practicum.playlistmaker.domain.use_case.GetTrackListUseCase
import com.practicum.playlistmaker.domain.use_case.SaveHistoryUseCase
import com.practicum.playlistmaker.domain.use_case.SaveTrackUseCase
import com.practicum.playlistmaker.ui.player.PlayerActivity

class SearchActivity : AppCompatActivity() {

    private val MAX_HISTORY_SIZE = 10

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    private var searchRunnable: Runnable? = null

    private var inputValue: String = INPUT_DEF

    private val tracks = mutableListOf<Track>()

    private lateinit var trackAdapter: TrackAdapter

    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderNotFound: ImageView
    private lateinit var refreshButton: ImageView
    private lateinit var inputEditText: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var rvTrack: RecyclerView
    private lateinit var historyView: LinearLayout
    private lateinit var loadTrack: TracksInteractor
    private lateinit var getTrack: GetSavedTrackUseCase
    private lateinit var getTrackListUseCase: GetTrackListUseCase
    private lateinit var saveTrackUseCase: SaveTrackUseCase
    private lateinit var saveHistoryUseCase: SaveHistoryUseCase
    lateinit var listener: OnSharedPreferenceChangeListener

    lateinit var historyAdapter: TrackAdapter

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_STATE, inputValue)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputValue = savedInstanceState.getString(INPUT_STATE, INPUT_DEF)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        loadTrack = Creator.provideTracksInteractor()
        getTrack = Creator.provideGetSavedTrack(this)
        saveTrackUseCase = Creator.provideSaveTrack(this)
        getTrackListUseCase = Creator.provideGetTrackList(this)
        saveHistoryUseCase = Creator.provideSaveHistory(this)

        inputEditText = findViewById(R.id.inputEditText)
        placeholderMessage = findViewById(R.id.placeholderMessage)
        placeholderNotFound = findViewById(R.id.placeholderNotFound)
        refreshButton = findViewById(R.id.refreshButton)
        progressBar = findViewById(R.id.progressBar)
        val backButton = findViewById<ImageView>(R.id.button_back)
        val clearButton = findViewById<ImageView>(R.id.clear_icon)
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        rvTrack = findViewById(R.id.rvTrack)
        historyView = findViewById(R.id.historyView)

        val playerIntent = Intent(this, PlayerActivity::class.java)

        historyAdapter = TrackAdapter {
            val chosenTrack: Track = it
            startActivity(playerIntent.putExtra("chosen_Track_Key", chosenTrack))
        }

        val clearHistoryButton = findViewById<ImageView>(R.id.clearHistoryButton)
        val foundTrack = findViewById<RecyclerView>(R.id.foundTrack)
        foundTrack.adapter = historyAdapter

        trackAdapter = TrackAdapter {
            val chosenTrack: Track = it
            if (clickDebounce()) {
                startActivity(playerIntent.putExtra("chosen_Track_Key", chosenTrack))
                saveTrackUseCase.execute(it, listener)
            }
        }

        trackAdapter.savedList = tracks

        rvTrack.adapter = trackAdapter

        inputEditText.setText(inputValue)

        doHistory()

        refreshButton.setOnClickListener {
            requestState()
        }

        searchRunnable = Runnable{requestState()}

        clearHistoryButton.setOnClickListener {
            historyAdapter.savedList.clear()
            historyAdapter.notifyDataSetChanged()
            historyView.visibility = View.GONE
        }

        clearButton.setOnClickListener {
            handler.removeCallbacks(searchRunnable!!)
            inputEditText.setText("")
            tracks.clear()
            progressBar.visibility = View.GONE
            trackAdapter.notifyDataSetChanged()
            historyAdapter.notifyDataSetChanged()
            showMessage("", null, "")
            inputMethodManager?.hideSoftInputFromWindow(clearButton.windowToken, 0)
        }

        backButton.setOnClickListener {
            finish()
        }

        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            historyView.visibility =
                if (hasFocus && inputEditText.text.isEmpty() && !historyAdapter.savedList.isEmpty()) View.VISIBLE else View.GONE
        }

        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                searchDebounce()

                historyView.visibility = if (inputEditText.hasFocus() && s?.isEmpty() == true) View.VISIBLE else View.GONE

                rvTrack.visibility = if(inputEditText.text.isEmpty()) View.GONE else View.VISIBLE

                if(inputEditText.text.isEmpty()) {tracks.clear()}

                clearButton.visibility = clearButtonVisibility(s)

                val input = s.toString()
                inputValue = input
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        inputEditText.addTextChangedListener(searchTextWatcher)
    }

    private fun clickDebounce() : Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable!!)
        handler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun removeTrack(track: Track?) {
        historyAdapter.savedList.remove(track)
    }

    private fun showMessage(text: String, icon: Drawable?, additionalMessage: String) {
        if (text.isNotEmpty()) {
            placeholderMessage.visibility = View.VISIBLE
            placeholderNotFound.visibility = View.VISIBLE
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
            placeholderMessage.text = text
            placeholderNotFound.setImageDrawable(icon)
            if (additionalMessage.isNotEmpty()) {
                Toast.makeText(applicationContext, additionalMessage, Toast.LENGTH_LONG)
                    .show()
                refreshButton.visibility = View.VISIBLE
                refreshButton.setImageDrawable(getDrawable(R.drawable.refresh_button))
            }
        } else {
            placeholderMessage.visibility = View.GONE
            placeholderNotFound.visibility = View.GONE
            refreshButton.visibility = View.GONE
        }
    }

    override fun onStop() {
        super.onStop()
        saveHistoryUseCase.execute(historyAdapter.savedList)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (searchRunnable != null) {
            handler.removeCallbacks(searchRunnable!!)
            searchRunnable = null
        }
    }

    fun doHistory() {

        historyAdapter.savedList = getTrackListUseCase.execute() ?: emptyList<Track>().toMutableList()

        listener = OnSharedPreferenceChangeListener { sharedPreferences, key ->
            val track = getTrack.execute()
            removeTrack(track)
            historyAdapter.savedList.add(0, track!!)
            Toast.makeText(this, "Сохранено", Toast.LENGTH_SHORT)
                .show()
            if (historyAdapter.savedList.size > MAX_HISTORY_SIZE) {
                historyAdapter.savedList.removeAt(MAX_HISTORY_SIZE)
            }
            historyAdapter.notifyItemInserted(0)
        }
    }

    private fun requestState() {
        if (inputEditText.text.isNotEmpty()) {
            placeholderMessage.visibility = View.GONE
            placeholderNotFound.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            historyView.visibility = View.GONE
            rvTrack.visibility = View.GONE

            loadTrack.search(
                inputEditText.text.toString(),
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>){
                        runOnUiThread {
                            progressBar.visibility = View.GONE
                            tracks.clear()
                            rvTrack.visibility = View.VISIBLE
                            tracks.addAll(foundTracks)
                            trackAdapter.notifyDataSetChanged()
                            if (tracks.isEmpty()) {
                                showMessage(
                                    getString(R.string.nothing_found),
                                    getDrawable(R.drawable.nothing),
                                    ""
                                )
                            } else {
                                showMessage("", null, "")
                            }

                            //else {
                            //showMessage(
                            //getString(R.string.something_went_wrong),
                            //getDrawable(R.drawable.went_wrong),
                            //response.code().toString()
                            //)
                        }}
                }

                //override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                //progressBar.visibility = View.GONE
                //showMessage(
                //getString(R.string.something_went_wrong),
                //getDrawable(R.drawable.went_wrong),
                //t.message.toString()
            )
        }
    }
    companion object {
        private const val INPUT_STATE = "INPUT_STATE"
        private const val INPUT_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}