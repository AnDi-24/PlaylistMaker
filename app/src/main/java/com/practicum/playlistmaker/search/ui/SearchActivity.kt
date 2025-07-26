package com.practicum.playlistmaker.search.ui

import android.content.Intent
import android.graphics.drawable.Drawable
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
import androidx.lifecycle.ViewModelProvider
import com.practicum.playlistmaker.util.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivitySearchBinding
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.domain.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.use_case.GetTrackListUseCase
import com.practicum.playlistmaker.search.domain.use_case.SaveHistoryUseCase
import com.practicum.playlistmaker.player.ui.PlayerActivity
import com.practicum.playlistmaker.search.ui.model.SearchState


class SearchActivity : AppCompatActivity() {

    private var isClickAllowed = true

    private lateinit var binding: ActivitySearchBinding
    private var textWatcher: TextWatcher? = null
    private var viewModel: TracksViewModel? = null

    private val handler = Handler(Looper.getMainLooper())

    private val trackAdapter = TrackAdapter {
        val chosenTrack: Track = it
        if (clickDebounce()) {
            startActivity(playerIntent.putExtra("chosen_Track_Key", chosenTrack))
            viewModel?.saveTrackFromListener(it)
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

        loadTrack = Creator.provideTracksInteractor(this)
        getTrackListUseCase = Creator.provideGetTrackList(this)
        saveHistoryUseCase = Creator.provideSaveHistory(this)
        playerIntent = Intent(this, PlayerActivity::class.java)

        viewModel = ViewModelProvider(this, TracksViewModel.getFactory())
            .get(TracksViewModel::class.java)

        viewModel?.observeState()?.observe(this) {
            render(it)
        }

        viewModel?.observeShowToast()?.observe(this) {
            showToast(it)
        }

        viewModel?.observeRemoveTrack()?.observe(this){
            historyAdapter.savedList.remove(it)
        }

        viewModel?.observeHistoryAdd()?.observe(this){
            historyAdapter.savedList.add(0, it)
        }

        viewModel?.observeHistoryRemoveAt()?.observe(this){
            historyAdapter.savedList.removeAt(it)
        }

        viewModel?.observeGetTrackListFromHistory()?.observe(this){
            historyAdapter.savedList =
                getTrackListUseCase.execute() ?: emptyList<Track>().toMutableList()
        }

        viewModel?.observeUpdateHistory()?.observe(this){
            historyAdapter.notifyDataSetChanged()
        }

        val backButton = findViewById<ImageView>(R.id.button_back)

        viewModel?.doHistory(historyAdapter.savedList.size)

        backButton.setOnClickListener {
            finish()
        }

        binding.rvTrack.adapter = trackAdapter
        binding.foundTrack.adapter = historyAdapter
        binding.inputEditText.setText(inputValue)

        binding.refreshButton.setOnClickListener {
            viewModel?.requestState(binding.inputEditText.toString())
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
            viewModel?.clearButton()
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

                viewModel?.searchDebounce(changedText = s?.toString() ?: "")

                binding.historyView.visibility =
                    if (binding.inputEditText.hasFocus() && s?.isEmpty() == true) View.VISIBLE else View.GONE

                binding.rvTrack.visibility = if (binding.inputEditText.text.isEmpty()) View.GONE else View.VISIBLE

                if (binding.inputEditText.text.isEmpty()) {
                    viewModel?.clearTracks()
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
        saveHistoryUseCase.execute(historyAdapter.savedList)
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

    fun showEmpty(emptyMessage: String, icon: Drawable?) {
        binding.placeholderMessage.visibility = View.VISIBLE
        binding.placeholderNotFound.visibility = View.VISIBLE
        viewModel?.clearTracks()
        trackAdapter.savedList.clear()
        trackAdapter.notifyDataSetChanged()
        binding.placeholderMessage.text = emptyMessage
        binding.placeholderNotFound.setImageDrawable(icon)
    }

    fun showError(errorMessage: String, icon: Drawable?) {
        binding.placeholderMessage.visibility = View.VISIBLE
        binding.placeholderNotFound.visibility = View.VISIBLE
        viewModel?.clearTracks()
        trackAdapter.savedList.clear()
        trackAdapter.notifyDataSetChanged()
        binding.apply {
            placeholderMessage.text = errorMessage
            placeholderNotFound.setImageDrawable(icon)
            refreshButton.visibility = View.VISIBLE
            refreshButton.setImageDrawable(getDrawable(R.drawable.refresh_button))
        }
    }

    fun render(state: SearchState) {
        when(state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Error -> showError(state.errorMessage, state.icon)
            is SearchState.Empty -> showEmpty(state.emptyMessage, state.icon)
            is SearchState.Content ->showContent(state.tracks)
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