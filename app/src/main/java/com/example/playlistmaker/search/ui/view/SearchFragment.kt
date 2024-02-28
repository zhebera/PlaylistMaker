package com.example.playlistmaker.search.ui.view

import android.content.Context.INPUT_METHOD_SERVICE
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.search.domain.models.SearchState
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.utils.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.utils.KEY_TRACK_ID
import com.example.playlistmaker.utils.createJsonFromTrack
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private var textWatcher: TextWatcher? = null
    private val viewModel by viewModel<SearchViewModel>()
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding
        get() = _binding!!
    
    private val playlistAdapter = PlaylistSearchAdapter(object: PlaylistSearchAdapter.SearchClickListener{
        override fun onTrackClick(track: Track) {
            viewModel.addNewTrackToHistory(track)
            onTrackClickDebounce(track)
        }

        override fun onTrackLongClick(track: Track) = Unit
    })

    private val searchHistoryAdapter = PlaylistSearchAdapter(object: PlaylistSearchAdapter.SearchClickListener{
        override fun onTrackClick(track: Track) {
            onTrackClickDebounce(track)
        }

        override fun onTrackLongClick(track: Track) = Unit
    })

    private var savedSearchEditText: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, true) { track ->
            val bundle = bundleOf(KEY_TRACK_ID to createJsonFromTrack(track))
            findNavController().navigate(R.id.action_searchFragment_to_audioplayerFragment, bundle)
        }

        binding.etInput.setText(savedSearchEditText)

        viewModel.apply {
            searchState.observe(viewLifecycleOwner, ::renderState)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.historyState.observe(viewLifecycleOwner) {
                showHistory(it, false)
            }
        }

        binding.rvTrack.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvTrack.adapter = playlistAdapter

        binding.rvSearchHistory.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvSearchHistory.adapter = searchHistoryAdapter

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.ivClearIcon.isVisible = clearButtonVisibility(s)
                savedSearchEditText = binding.etInput.text.toString()

                binding.llSearchHistory.isVisible =
                    binding.etInput.hasFocus() && s?.isEmpty() == true

                if (!s.isNullOrEmpty())
                    viewModel.searchDebounce(
                        changedText = s.toString()
                    )
            }

            override fun afterTextChanged(s: Editable?) = Unit
        }

        textWatcher.let {
            binding.etInput.addTextChangedListener(it)
        }

        binding.etInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && binding.etInput.text.toString().isNotEmpty()) {
                viewModel.searchDebounce(binding.etInput.text.toString())
                closeKeybord()
                true
            } else {
                false
            }
        }

        binding.etInput.setOnFocusChangeListener { _, hasFocus ->
            val history = viewModel.historyState.value

            binding.llSearchHistory.isVisible =
                if (hasFocus && binding.etInput.text.isEmpty() && !history.isNullOrEmpty()) {
                    showHistory(history, true)
                    true
                } else
                    false
        }

        binding.btnSearchHistoryClear.setOnClickListener {
            viewModel.removeHistory()
            searchHistoryAdapter.notifyDataSetChanged()
            binding.llSearchHistory.isVisible = false
        }

        binding.ivClearIcon.setOnClickListener {
            clearAll()
        }

        binding.btnPlaylistCreate.setOnClickListener {
            viewModel.searchDebounce(binding.etInput.text.toString())
        }
    }

    private fun renderState(state: SearchState) {
        when (state) {
            is SearchState.Loading -> showLoading()
            is SearchState.Content -> showContent(state.data)
            is SearchState.Error -> showError()
            is SearchState.Empty -> showEmpty()
        }
    }

    private fun showLoading() {
        binding.rvTrack.isVisible = false
        binding.progressBar.isVisible = true
    }

    private fun showError() {
        binding.progressBar.isVisible = false
        binding.rvTrack.isVisible = true
        showErrorMessage(
            getString(R.string.failed_connection),
            requireContext().getDrawable(R.drawable.failed_connection),
            true
        )
    }

    private fun showContent(listTrack: List<Track>) {
        binding.rvTrack.isVisible = true
        binding.llPlaceHolder.isVisible = false
        binding.progressBar.isVisible = false

        with(playlistAdapter) {
            tracks.clear()
            tracks.addAll(listTrack)
            notifyDataSetChanged()
        }
    }

    private fun showHistory(historyList: List<Track>, show: Boolean) {
        if (show) {
            binding.rvTrack.isVisible = false
            binding.llPlaceHolder.isVisible = false
            binding.progressBar.isVisible = false

            with(searchHistoryAdapter) {
                tracks.clear()
                tracks.addAll(historyList)
                notifyDataSetChanged()
            }
        }
    }

    private fun showEmpty() {
        showErrorMessage(
            getString(R.string.not_found),
            requireContext().getDrawable(R.drawable.not_found),
            false
        )
    }

    private fun showErrorMessage(text: String, image: Drawable?, update: Boolean) {
        Glide.with(this)
            .load(image)
            .transform(CenterCrop())
            .into(binding.ivPlaceHolder)

        with(binding) {
            tvPlaceholderMessage.text = text
            llPlaceHolder.isVisible = true
            progressBar.isVisible = false
            btnPlaylistCreate.isVisible = update
        }
    }

    private fun hideErrorMessage() {
        binding.llPlaceHolder.isVisible = false
    }

    private fun clearButtonVisibility(s: CharSequence?): Boolean {
        hideErrorMessage()
        return !s.isNullOrEmpty()
    }

    private fun clearAll() {
        binding.etInput.text.clear()
        showContent(listOf())
        closeKeybord()
        binding.etInput.clearFocus()
        hideErrorMessage()
    }

    private fun closeKeybord() {
        val inputMethodManager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.etInput.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearAll()
        viewModel.finishSearch()
        textWatcher.let { binding.etInput.removeTextChangedListener(it) }
    }
}