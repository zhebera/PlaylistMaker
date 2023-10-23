package com.example.playlistmaker.search.ui.viewmodel

import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.models.SearchState

class SearchViewModel(private val searchInteractor: SearchInteractor) : ViewModel() {

    private val handler = Handler(Looper.getMainLooper())

    private val _historyState = MutableLiveData(searchInteractor.getSavedHistory())
    val historyState: LiveData<List<Track>> = _historyState

    private val _searchState = MutableLiveData<SearchState>()

    val searchState: LiveData<SearchState> = _searchState

    private val _toastState = MutableLiveData<String>()
    val toastState: LiveData<String> = _toastState

    private val listTrack = ArrayList<Track>()
    private var latestSearchTrack: String? = null

    private fun searchRequest(newSearchTrack: String) {
        if (newSearchTrack.isNotEmpty()) {
            renderState(
                SearchState.Loading
            )
        }

        searchInteractor.searchTrack(
            newSearchTrack
        ) { tracks, errorMessage ->
            handler.post {
                if (tracks != null) {
                    listTrack.clear()
                    listTrack.addAll(tracks)
                }

                when {
                    errorMessage != null -> {
                        renderState(SearchState.Error)
                    }

                    listTrack?.isEmpty() == true -> {
                        renderState(SearchState.Empty)
                    }

                    else -> {
                        renderState(SearchState.Content(listTrack))
                    }
                }
            }
        }
    }

    private fun renderState(state: SearchState) {
        _searchState.postValue(state)
    }

    private fun changeHistory() {
        _historyState.postValue(searchInteractor.getSavedHistory())
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun searchDebounce(changedText: String) {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { searchRequest(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY

        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )

        if (latestSearchTrack == changedText)
            return

        latestSearchTrack = changedText
    }

    fun finishSearch(){
        listTrack.clear()
        renderState(SearchState.Content(listTrack))
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun addNewTrackToHistory(track: Track) {
        searchInteractor.addNewTrackToHistory(track)
        changeHistory()
    }

    fun removeHistory() {
        searchInteractor.removeHistory()
        changeHistory()
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private val SEARCH_REQUEST_TOKEN = Any()
    }
}