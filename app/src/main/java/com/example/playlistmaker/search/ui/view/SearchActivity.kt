package com.example.playlistmaker.search.ui.view

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.player.ui.view.AudioplayerActivity
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.search.domain.models.SearchState
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.utils.KEY_TRACK_ID
import com.example.playlistmaker.utils.createJsonFromTrack
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchActivity : AppCompatActivity() {
    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    private lateinit var searchEditTxt: EditText
    private lateinit var btnPlaceHolderUpdate: Button
    private lateinit var placeHolder: LinearLayout
    private lateinit var placeHolderImage: ImageView
    private lateinit var placeHolderMessage: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var trackRecyclerView: RecyclerView
    private var textWatcher: TextWatcher? = null
    private var isClicked = true
    private val handler = Handler(Looper.getMainLooper())
    private val viewModel by viewModel<SearchViewModel>()

    private lateinit var binding: ActivitySearchBinding

    private val playlistAdapter = SearchAdapter(
        object : SearchAdapter.SearchClickListener {
            override fun onTrackClick(track: Track) {
                viewModel.addNewTrackToHistory(track)
                startActivity(track)
            }
        }
    )

    private val searchHistoryAdapter = SearchAdapter(
        object : SearchAdapter.SearchClickListener {
            override fun onTrackClick(track: Track) {
                startActivity(track)
            }
        }
    )

    private var savedSearchEditText: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            savedSearchEditText = savedInstanceState.getString("SAVED_SEARCH_EDIT_TXT")
        }

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        placeHolder = binding.placeHolder
        placeHolderImage = binding.placeHolderImage
        placeHolderMessage = binding.placeHolderMessage
        btnPlaceHolderUpdate = binding.placeHolderUpdateButton
        searchEditTxt = binding.inputEditText
        progressBar = binding.progressBar
        trackRecyclerView = binding.trackRecyclerView
        val btnMainActivity = binding.btnMainActivity
        val btnClearEditTxt = binding.clearIcon
        val searchHistory = binding.searchHistory
        val searchHistoryRecyclerView = binding.searchHistoryRecyclerView
        val btnClearSearchHistory = binding.searchHistoryClearButton

        btnMainActivity.setOnClickListener {
            finish()
        }

        viewModel.searchState.observe(this) {
            renderState(it)
        }

        viewModel.toastState.observe(this) {
            showToast(it)
        }

        viewModel.historyState.observe(this) {
            showHistory(it, false)
        }

        trackRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackRecyclerView.adapter = playlistAdapter

        searchHistoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        searchHistoryRecyclerView.adapter = searchHistoryAdapter

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnClearEditTxt.visibility = clearButtonVisibility(s)
                savedSearchEditText = searchEditTxt.text.toString()

                searchHistory.visibility =
                    if (searchEditTxt.hasFocus() && s?.isEmpty() == true)
                        View.VISIBLE
                    else
                        View.GONE

                if (!savedSearchEditText.isNullOrEmpty())
                    viewModel?.searchDebounce(
                        changedText = s?.toString() ?: ""
                    )
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        textWatcher.let {
            searchEditTxt.addTextChangedListener(it)
        }

        searchEditTxt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && !savedSearchEditText.isNullOrEmpty()) {
                viewModel.searchDebounce(savedSearchEditText!!)
                true
            }
            false
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
            searchHistoryAdapter.notifyDataSetChanged()
            searchHistory.visibility = View.GONE
        }

        btnClearEditTxt.setOnClickListener {
            searchEditTxt.text.clear()
            showContent(listOf())
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchEditTxt.windowToken, 0)
            searchEditTxt.clearFocus()
            hideErrorMessage()
        }

        btnPlaceHolderUpdate.setOnClickListener {
            viewModel.searchDebounce(savedSearchEditText!!)
        }
    }

    private fun startActivity(track: Track) {
        if (clickDebounce()) {
            val audioplayerIntent = Intent(this@SearchActivity, AudioplayerActivity::class.java)
            audioplayerIntent.putExtra(KEY_TRACK_ID, createJsonFromTrack(track))
            startActivity(audioplayerIntent)
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
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
            getDrawable(R.drawable.failed_connection),
            true
        )
    }

    private fun showContent(listTrack: List<Track>) {
        trackRecyclerView.visibility = View.VISIBLE
        placeHolder.visibility = View.GONE
        progressBar.visibility = View.GONE

        playlistAdapter.tracks.clear()
        playlistAdapter.tracks.addAll(listTrack)
        playlistAdapter.notifyDataSetChanged()
    }

    private fun showHistory(historyList: List<Track>, show: Boolean) {
        if (show) {
            trackRecyclerView.visibility = View.GONE
            placeHolder.visibility = View.GONE
            progressBar.visibility = View.GONE

            searchHistoryAdapter.tracks.clear()
            searchHistoryAdapter.tracks.addAll(historyList)
            searchHistoryAdapter.notifyDataSetChanged()
        }
    }

    private fun showEmpty() {
        showErrorMessage(
            getString(R.string.not_found),
            getDrawable(R.drawable.not_found),
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

    private fun clickDebounce(): Boolean {
        val current = isClicked
        if (isClicked) {
            isClicked = false
            handler.postDelayed({ isClicked = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
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

    override fun onDestroy() {
        super.onDestroy()
        textWatcher.let { searchEditTxt.removeTextChangedListener(it) }
    }
}

