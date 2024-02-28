package com.example.playlistmaker.search.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.search.domain.api.SearchInteractor
import com.example.playlistmaker.search.domain.models.SearchState
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.launch

class SearchViewModel(private val searchInteractor: SearchInteractor) : ViewModel() {

    private val _historyState = MutableLiveData(searchInteractor.getSavedHistory())
    val historyState: LiveData<List<Track>> = _historyState

    private val _searchState = MutableLiveData<SearchState>()
    val searchState: LiveData<SearchState> = _searchState

    private val trackSearchDebounce = debounce<String>(
        SEARCH_DEBOUNCE_DELAY,
        viewModelScope,
        true
    ) { changedText ->
        searchRequest(changedText)
    }

    private val listTrack = ArrayList<Track>()
    private var latestSearchTrack: String? = null

    private fun searchRequest(newSearchTrack: String) {
        if (newSearchTrack.isNotEmpty()) {
            renderState(
                SearchState.Loading
            )
        }

        viewModelScope.launch {
            searchInteractor.searchTrack(newSearchTrack).collect { pair ->
                processResult(pair.first, pair.second)
            }
        }
    }

    private fun processResult(tracks: List<Track>?, errorMessage: String?) {
        if (tracks != null) {
            listTrack.clear()
            listTrack.addAll(tracks)
        }

        when {
            errorMessage != null -> {
                renderState(SearchState.Error)
            }

            listTrack.isEmpty() -> {
                renderState(SearchState.Empty)
            }

            else -> {
                renderState(SearchState.Content(listTrack))
            }
        }

    }

    private fun renderState(state: SearchState) {
        _searchState.postValue(state)
    }

    private fun changeHistory() {
        _historyState.postValue(searchInteractor.getSavedHistory())
    }

    fun searchDebounce(changedText: String) {

        if (latestSearchTrack == changedText)
            return

        trackSearchDebounce(changedText)

        latestSearchTrack = changedText
    }

    fun finishSearch() {
        listTrack.clear()
        renderState(SearchState.Content(listTrack))
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
    }
}