package com.practicum.playlistmaker.search.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.ui.model.SearchState
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchActivity : AppCompatActivity() {

    private var isClickAllowed = true

    private lateinit var binding: ActivitySearchBinding

    private var textWatcher: TextWatcher? = null
    private val viewModel by viewModel<SearchViewModel>()

    private val handler = Handler(Looper.getMainLooper())

    private val trackAdapter = TrackAdapter {
        val chosenTrackToString = Json.encodeToString(it)
        if (clickDebounce()) {
            startActivity(playerIntent.putExtra("chosen_Track_Key", chosenTrackToString))
            viewModel.saveTrackFromListener(it)
        }
    }

    private var historyAdapter = TrackAdapter {
        val chosenTrackToString = Json.encodeToString(it)
        startActivity(playerIntent.putExtra("chosen_Track_Key", chosenTrackToString))
    }

    private var inputValue: String = INPUT_DEF
    lateinit var playerIntent: Intent

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
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager

        historyAdapter.savedList = viewModel.getTrackListFromPref() ?: emptyList<Track>().toMutableList()


        playerIntent = Intent(this, PlayerActivity::class.java)

        viewModel.observeState().observe(this) {
            render(it)
        }

        viewModel.observeShowToast().observe(this) {
            showToast(it)
        }

        val backButton = findViewById<ImageView>(R.id.button_back)


        backButton.setOnClickListener {
            finish()
        }

        binding.rvTrack.adapter = trackAdapter
        binding.foundTrack.adapter = historyAdapter
        binding.inputEditText.setText(inputValue)

        binding.refreshButton.setOnClickListener {
            viewModel.requestState(binding.inputEditText.toString())
            binding.refreshButton.visibility = View.GONE
        }

        binding.clearHistoryButton.setOnClickListener {
            historyAdapter.savedList.clear()
            historyAdapter.notifyDataSetChanged()
            binding.historyView.visibility = View.GONE
        }

        binding.clearHistoryButton.setOnClickListener {
            historyAdapter.savedList.clear()
            historyAdapter.notifyDataSetChanged()
            binding.historyView.visibility = View.GONE
        }

        binding.clearIcon.setOnClickListener {
            binding.inputEditText.setText("")
            viewModel.clearButton()
            inputMethodManager?.hideSoftInputFromWindow(binding.clearIcon.windowToken, 0)
        }

        binding.inputEditText.setOnFocusChangeListener { view, hasFocus ->
            binding.historyView.visibility =
                if (hasFocus && binding.inputEditText.text.isEmpty() && !historyAdapter.savedList.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.inputEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                viewModel.searchDebounce(changedText = s?.toString() ?: "")

                binding.historyView.visibility =
                    if (binding.inputEditText.hasFocus() && s?.isEmpty() == true) View.VISIBLE else View.GONE

                binding.rvTrack.visibility = if (binding.inputEditText.text.isEmpty()) View.GONE else View.VISIBLE

                if (binding.inputEditText.text.isEmpty()) {
                    viewModel.clearTracks()
                }

                binding.clearIcon.visibility = clearButtonVisibility(s)

                val input = s.toString()
                inputValue = input
            }

            override fun afterTextChanged(s: Editable?) {
                binding.apply {
                    placeholderMessage.visibility = View.GONE
                    placeholderNotFound.visibility = View.GONE
                    refreshButton.visibility = View.GONE
                }

            }
        })

        textWatcher?.let { binding.inputEditText.addTextChangedListener(it) }

    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { binding.inputEditText.removeTextChangedListener(it) }
        viewModel.saveHistoryFromAdapter(historyAdapter.savedList)
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

    fun showContent(tracks: List<Track>) {
        binding.apply {
            progressBar.visibility = View.GONE
            placeholderMessage.visibility = View.GONE
            placeholderNotFound.visibility = View.GONE
            refreshButton.visibility = View.GONE
            rvTrack.visibility = View.VISIBLE
        }
        trackAdapter.savedList.clear()
        trackAdapter.savedList.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    fun showLoading() {
        binding.apply {
            placeholderMessage.visibility = View.GONE
            placeholderNotFound.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            historyView.visibility = View.GONE
            rvTrack.visibility = View.GONE
        }
    }

    fun showEmpty() {
        binding.placeholderMessage.visibility = View.VISIBLE
        binding.placeholderNotFound.visibility = View.VISIBLE
        viewModel.clearTracks()
        trackAdapter.savedList.clear()
        trackAdapter.notifyDataSetChanged()
        binding.placeholderMessage.text = getString(R.string.nothing_found)
        binding.placeholderNotFound.setImageDrawable(getDrawable(R.drawable.nothing))
    }

    fun showError() {
        binding.placeholderMessage.visibility = View.VISIBLE
        binding.placeholderNotFound.visibility = View.VISIBLE
        viewModel.clearTracks()
        trackAdapter.savedList.clear()
        trackAdapter.notifyDataSetChanged()
        binding.apply {
            placeholderMessage.text = getString(R.string.something_went_wrong)
            placeholderNotFound.setImageDrawable(getDrawable(R.drawable.went_wrong))
            refreshButton.visibility = View.VISIBLE
            refreshButton.setImageDrawable(getDrawable(R.drawable.refresh_button))
        }
    }

    fun removeAt(position: Int){
        historyAdapter.savedList.removeAt(position)
        historyAdapter.notifyDataSetChanged()
    }
    fun historyActions(track: Track){
        historyAdapter.savedList.remove(track)
        historyAdapter.notifyDataSetChanged()
        historyAdapter.savedList.add(0, track)
        historyAdapter.notifyDataSetChanged()
        viewModel.cleanHistory(historyAdapter.savedList.size)
        historyAdapter.notifyDataSetChanged()
    }

    fun render(state: SearchState) {
        when(state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Error -> showError()
            is SearchState.Empty -> showEmpty()
            is SearchState.Content ->showContent(state.tracks)
            is SearchState.HistoryActions -> historyActions(state.track)
            is SearchState.RemoveAt -> removeAt(state.position)
        }
    }

    fun showToast(text: String?) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT)
            .show()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private const val INPUT_STATE = "INPUT_STATE"
        private const val INPUT_DEF = ""
    }
}