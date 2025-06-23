package com.practicum.playlistmaker


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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    companion object {
        private const val INPUT_STATE = "INPUT_STATE"
        private const val INPUT_DEF = ""
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }

    private var isClickAllowed = true

    private val handler = Handler(Looper.getMainLooper())

    private var searchRunnable: Runnable? = null

    private val iTunesUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(iTunesApi::class.java)

    private var inputValue: String = INPUT_DEF

    private val tracks = mutableListOf<Track>()

    private lateinit var trackAdapter: TrackAdapter

    private lateinit var placeholderMessage: TextView
    private lateinit var placeholderNotFound: ImageView
    private lateinit var refreshButton: ImageView
    private lateinit var inputEditText: EditText
    private lateinit var searchHistory: SearchHistory
    private lateinit var progressBar: ProgressBar
    private lateinit var rvTrack: RecyclerView
    private lateinit var historyView: LinearLayout

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

        searchHistory = SearchHistory(this)
        val playerIntent = Intent(this, PlayerActivity::class.java)

        trackAdapter = TrackAdapter{
            val chosenTrack: Track = it
            if (clickDebounce()) {
                startActivity(playerIntent.putExtra("chosen_Track_Key", chosenTrack))
                searchHistory.saveTrack(it)
            }
        }

        historyAdapter = TrackAdapter{
            val chosenTrack: Track = it
            startActivity(playerIntent.putExtra("chosen_Track_Key", chosenTrack))
        }

        val clearHistoryButton = findViewById<ImageView>(R.id.clearHistoryButton)
        val foundTrack = findViewById<RecyclerView>(R.id.foundTrack)
        foundTrack.adapter = historyAdapter

        savedHistory = getSharedPreferences(SEARCH_HISTORY, MODE_PRIVATE)

        searchHistory.doHistory()

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


        clearHistoryButton.setOnClickListener {
            historyAdapter.savedList.clear()
            historyAdapter.notifyDataSetChanged()
            historyView.visibility = View.GONE
        }

        trackAdapter.savedList = tracks

        rvTrack.adapter = trackAdapter

        inputEditText.setText(inputValue)

        //inputEditText.setOnEditorActionListener { _, actionId, _ ->
            //if (actionId == EditorInfo.IME_ACTION_DONE) {
                //if (inputEditText.text.isNotEmpty()) {
                    //progressBar.visibility = View.VISIBLE
                    //requestState()
                //}
                //true
            //}
            //false
        //}

        refreshButton.setOnClickListener {
            requestState()
        }

        searchRunnable = Runnable{requestState()}

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
        searchHistory.saveHistory()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (searchRunnable != null) {
            handler.removeCallbacks(searchRunnable!!)
            searchRunnable = null
        }
    }

    private fun requestState(){
        if (inputEditText.text.isNotEmpty()) {
            placeholderMessage.visibility = View.GONE
            placeholderNotFound.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            historyView.visibility = View.GONE
            rvTrack.visibility = View.GONE

            iTunesService.search(inputEditText.text.toString())
                .enqueue(object : Callback<TracksResponse> {
                    override fun onResponse(
                        call: Call<TracksResponse>,
                        response: Response<TracksResponse>
                    ) {
                        progressBar.visibility = View.GONE
                        if (response.isSuccessful) {
                            val responseResults = response.body()?.results ?: emptyList()
                            tracks.clear()
                            if (responseResults.isNotEmpty() == true) {
                                rvTrack.visibility = View.VISIBLE
                                tracks.addAll(responseResults)
                                trackAdapter.notifyDataSetChanged()
                            }
                            if (tracks.isEmpty()) {
                                showMessage(
                                    getString(R.string.nothing_found),
                                    getDrawable(R.drawable.nothing),
                                    ""
                                )
                            } else {
                                showMessage("", null, "")
                            }
                        } else {
                            showMessage(
                                getString(R.string.something_went_wrong),
                                getDrawable(R.drawable.went_wrong),
                                response.code().toString()
                            )
                        }
                    }

                    override fun onFailure(call: Call<TracksResponse>, t: Throwable) {
                        progressBar.visibility = View.GONE
                        showMessage(
                            getString(R.string.something_went_wrong),
                            getDrawable(R.drawable.went_wrong),
                            t.message.toString()
                        )
                    }
                })
        }
    }
}