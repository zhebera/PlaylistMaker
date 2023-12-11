package com.example.playlistmaker.search.ui.view

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.player.ui.view.AudioplayerActivity
import com.example.playlistmaker.search.domain.models.SearchState
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.utils.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.utils.KEY_TRACK_ID
import com.example.playlistmaker.utils.createJsonFromTrack
import com.example.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private lateinit var searchEditTxt: EditText
    private lateinit var btnPlaceHolderUpdate: Button
    private lateinit var placeHolder: LinearLayout
    private lateinit var placeHolderImage: ImageView
    private lateinit var placeHolderMessage: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var btnClearInput: ImageView
    private var textWatcher: TextWatcher? = null
    private val viewModel by viewModel<SearchViewModel>()
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private var _binding: FragmentSearchBinding? = null
    private val binding: FragmentSearchBinding
        get() = _binding!!

    private var playlistAdapter: PlaylistAdapter? = null
    private var searchHistoryAdapter: PlaylistAdapter? = null

    private var savedSearchEditText: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            val audioplayerIntent = Intent(requireContext(), AudioplayerActivity::class.java)
            audioplayerIntent.putExtra(KEY_TRACK_ID, createJsonFromTrack(track))
            startActivity(audioplayerIntent)
        }

        playlistAdapter = PlaylistAdapter { track ->
            viewModel.addNewTrackToHistory(track)
            onTrackClickDebounce(track)
        }

        searchHistoryAdapter = PlaylistAdapter { track ->
            onTrackClickDebounce(track)
        }

        placeHolder = binding.llPlaceHolder
        placeHolderImage = binding.ivPlaceHolder
        placeHolderMessage = binding.tvPlaceholderMessage
        btnPlaceHolderUpdate = binding.btnPlaceholderUpdate
        searchEditTxt = binding.etInput
        progressBar = binding.progressBar
        trackRecyclerView = binding.rvTrack
        btnClearInput = binding.ivClearIcon
        val searchHistory = binding.llSearchHistory
        val searchHistoryRecyclerView = binding.rvSearchHistory
        val btnClearSearchHistory = binding.btnSearchHistoryClear
        searchEditTxt.setText(savedSearchEditText)

        viewModel.searchState.observe(viewLifecycleOwner) {
            renderState(it)
        }

        viewModel.toastState.observe(viewLifecycleOwner) {
            showToast(it)
        }

        viewModel.historyState.observe(viewLifecycleOwner) {
            showHistory(it, false)
        }

        trackRecyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        trackRecyclerView.adapter = playlistAdapter

        searchHistoryRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        searchHistoryRecyclerView.adapter = searchHistoryAdapter

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnClearInput.visibility = clearButtonVisibility(s)
                savedSearchEditText = searchEditTxt.text.toString()

                searchHistory.visibility =
                    if (searchEditTxt.hasFocus() && s?.isEmpty() == true)
                        View.VISIBLE
                    else
                        View.GONE

                if (!s.isNullOrEmpty())
                    viewModel.searchDebounce(
                        changedText = s.toString()
                    )
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        textWatcher.let {
            searchEditTxt.addTextChangedListener(it)
        }

        searchEditTxt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && !searchEditTxt.text.toString().isNullOrEmpty()) {
                viewModel.searchDebounce(searchEditTxt.text.toString())
                closeKeybord()
                true
            } else {
                false
            }
        }

        searchEditTxt.setOnFocusChangeListener { view, hasFocus ->
            val history = viewModel.historyState.value

            searchHistory.visibility =
                if (hasFocus && searchEditTxt.text.isEmpty() && !history.isNullOrEmpty()) {
                    showHistory(history, true)
                    View.VISIBLE
                } else
                    View.GONE
        }

        btnClearSearchHistory.setOnClickListener {
            viewModel.removeHistory()
            searchHistoryAdapter?.notifyDataSetChanged()
            searchHistory.visibility = View.GONE
        }

        btnClearInput.setOnClickListener {
            clearAll()
        }

        btnPlaceHolderUpdate.setOnClickListener {
            viewModel.searchDebounce(searchEditTxt.text.toString())
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

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showLoading() {
        trackRecyclerView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    private fun showError() {
        progressBar.visibility = View.GONE
        trackRecyclerView.visibility = View.VISIBLE
        showErrorMessage(
            getString(R.string.failed_connection),
            requireContext().getDrawable(R.drawable.failed_connection),
            true
        )
    }

    private fun showContent(listTrack: List<Track>) {
        trackRecyclerView.visibility = View.VISIBLE
        placeHolder.visibility = View.GONE
        progressBar.visibility = View.GONE


        playlistAdapter?.tracks?.clear()
        playlistAdapter?.tracks?.addAll(listTrack)
        playlistAdapter?.notifyDataSetChanged()
    }

    private fun showHistory(historyList: List<Track>, show: Boolean) {
        if (show) {
            trackRecyclerView.visibility = View.GONE
            placeHolder.visibility = View.GONE
            progressBar.visibility = View.GONE

            searchHistoryAdapter?.tracks?.clear()
            searchHistoryAdapter?.tracks?.addAll(historyList)
            searchHistoryAdapter?.notifyDataSetChanged()
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
            .into(placeHolderImage)

        placeHolderMessage.text = text
        placeHolder.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        if (update)
            btnPlaceHolderUpdate.visibility = View.VISIBLE
        else
            btnPlaceHolderUpdate.visibility = View.GONE
    }

    private fun hideErrorMessage() {
        placeHolder.visibility = View.GONE
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        hideErrorMessage()
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun clearAll() {
        searchEditTxt.text.clear()
        showContent(listOf())
        closeKeybord()
        searchEditTxt.clearFocus()
        hideErrorMessage()
    }

    private fun closeKeybord() {
        val inputMethodManager = requireContext().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(searchEditTxt.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        clearAll()
        viewModel.finishSearch()
        textWatcher.let { searchEditTxt.removeTextChangedListener(it) }
    }
}