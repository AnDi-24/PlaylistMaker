package com.practicum.playlistmaker.search.ui

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.PlayerFragment
import com.practicum.playlistmaker.search.domain.models.Track
import com.practicum.playlistmaker.search.ui.model.SearchState
import com.practicum.playlistmaker.util.debounce
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class SearchFragment : Fragment() {

    private var textWatcher: TextWatcher? = null
    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private val trackAdapter = TrackAdapter {
        onTrackClickDebounce(it)
    }

    private var historyAdapter = TrackAdapter {
        val chosenTrackToString = Json.encodeToString(it)
        findNavController().navigate(R.id.action_searchFragment_to_playerFragment,
            PlayerFragment.createArgs(chosenTrackToString))
    }

    private var inputValue: String = INPUT_DEF
    lateinit var playerIntent: Intent

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_STATE, inputValue)
    }

    private var _binding: FragmentSearchBinding? = null
    private val binding get() =_binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false) { track ->
            viewModel.saveTrackFromListener(track)
            val chosenTrackToString = Json.encodeToString(track)
            findNavController().navigate(R.id.action_searchFragment_to_playerFragment,
                PlayerFragment.createArgs(chosenTrackToString))
        }

        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager

        historyAdapter.savedList = viewModel.getTrackListFromPref() ?: emptyList<Track>().toMutableList()

        playerIntent = Intent(requireContext(), PlayerFragment::class.java)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.observeShowToast().observe(viewLifecycleOwner) {
            showToast(it)
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

                binding.rvTrack.visibility =
                    if (binding.inputEditText.text.isEmpty()) View.GONE else View.VISIBLE

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

    override fun onDestroyView() {
        super.onDestroyView()
        textWatcher?.let { binding.inputEditText.removeTextChangedListener(it) }
        viewModel.saveHistoryFromAdapter(historyAdapter.savedList)
        _binding = null
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
        binding.placeholderNotFound.setImageDrawable(getDrawable(requireContext(), R.drawable.nothing))
    }

    fun showError() {
        binding.placeholderMessage.visibility = View.VISIBLE
        binding.placeholderNotFound.visibility = View.VISIBLE
        viewModel.clearTracks()
        trackAdapter.savedList.clear()
        trackAdapter.notifyDataSetChanged()
        binding.apply {
            placeholderMessage.text = getString(R.string.something_went_wrong)
            placeholderNotFound.setImageDrawable(getDrawable(requireContext(),R.drawable.went_wrong))
            refreshButton.visibility = View.VISIBLE
            refreshButton.setImageDrawable(getDrawable(requireContext(),R.drawable.refresh_button))
        }
    }

    fun removeAt(position: Int) {
        historyAdapter.savedList.removeAt(position)
        historyAdapter.notifyDataSetChanged()
    }

    fun historyActions(track: Track) {

        val trackId = track.trackId
        val isTrackExists = historyAdapter.savedList.any { it.trackId == trackId }

        if (!isTrackExists) {
            historyAdapter.savedList.add(0, track)
            viewModel.cleanHistory(historyAdapter.savedList.size)
            historyAdapter.notifyDataSetChanged()
        } else {
            historyAdapter.savedList.removeIf { it.trackId == trackId }
            historyAdapter.savedList.add(0, track)
            viewModel.cleanHistory(historyAdapter.savedList.size)
            historyAdapter.notifyDataSetChanged()
        }
    }

    fun render(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Error -> showError()
            is SearchState.Empty -> showEmpty()
            is SearchState.Content -> showContent(state.tracks)
            is SearchState.HistoryActions -> historyActions(state.track)
            is SearchState.RemoveAt -> removeAt(state.position)
        }
    }

    fun showToast(text: String?) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT)
            .show()
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 100L
        private const val INPUT_STATE = "INPUT_STATE"
        private const val INPUT_DEF = ""
    }
}