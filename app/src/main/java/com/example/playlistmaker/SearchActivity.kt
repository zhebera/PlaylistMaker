package com.example.playlistmaker

import Track
import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

const val SEARCH_HISTORY_PLAYLIST = "search_history_playlist"
const val SEARCH_HISTORY_NEW_TRACK = "search_history_new_track"

class SearchActivity : AppCompatActivity() {
    companion object {
        private const val BASE_URL = "https://itunes.apple.com"
    }

    private lateinit var searchEditTxt: EditText
    private lateinit var btnPlaceHolderUpdate: Button
    private lateinit var placeHolder: LinearLayout
    private lateinit var placeHolderImage: ImageView
    private lateinit var placeHolderMessage: TextView
    private lateinit var playlistAdapter: PlaylistAdapter
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter
    private lateinit var searchNewTrackListener: OnSharedPreferenceChangeListener

    private var savedSearchEditText: String? = null

    private val playlistTracks = mutableListOf<Track>()
    private val searchHistoryTracks = mutableListOf<Track>()
    private val playlistRetrofit = PlaylistRetrofit(BASE_URL).playlistRetrofit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        placeHolder = findViewById(R.id.placeHolder)
        placeHolderImage = findViewById(R.id.placeHolderImage)
        placeHolderMessage = findViewById(R.id.placeHolderMessage)
        btnPlaceHolderUpdate = findViewById(R.id.placeHolderUpdateButton)
        searchEditTxt = findViewById(R.id.inputEditText)
        val btnMainActivity = findViewById<ImageView>(R.id.btn_main_activity)
        val btnClearEditTxt = findViewById<ImageView>(R.id.clearIcon)
        val trackRecyclerView = findViewById<RecyclerView>(R.id.trackRecyclerView)
        val searchHistory = findViewById<LinearLayout>(R.id.searchHistory)
        val searchHistoryRecyclerView = findViewById<RecyclerView>(R.id.searchHistoryRecyclerView)
        val btnClearSearchHistory = findViewById<Button>(R.id.searchHistoryClearButton)
        val searchHistorySharedPrefNewTrack = getSharedPreferences(SEARCH_HISTORY_NEW_TRACK, MODE_PRIVATE)
        val searchHistorySharedPrefPlaylist = getSharedPreferences(SEARCH_HISTORY_PLAYLIST, MODE_PRIVATE)
        playlistAdapter = PlaylistAdapter(searchHistorySharedPrefNewTrack)
        searchHistoryAdapter = SearchHistoryAdapter()

        val searchHistoryJson = searchHistorySharedPrefPlaylist.getString(SEARCH_HISTORY_PLAYLIST, null)
        searchHistoryTracks.addAll(createListTrackFromJson(searchHistoryJson))

        if (savedInstanceState != null) {
            savedSearchEditText = savedInstanceState.getString("SAVED_SEARCH_EDIT_TXT")
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnClearEditTxt.visibility = clearButtonVisibility(s)
                savedSearchEditText = searchEditTxt.text.toString()

                searchHistory.visibility =
                    if (searchEditTxt.hasFocus() && s?.isEmpty() == true)
                        View.VISIBLE
                    else
                        View.GONE
            }

            override fun afterTextChanged(s: Editable?) = Unit
        }

        playlistAdapter.listTrack = playlistTracks
        trackRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackRecyclerView.adapter = playlistAdapter

        searchHistoryAdapter.listTrack = searchHistoryTracks
        searchHistoryRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        searchHistoryRecyclerView.adapter = searchHistoryAdapter

        searchEditTxt.setText(savedSearchEditText)
        searchEditTxt.addTextChangedListener(textWatcher)

        searchEditTxt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                updateListTracks()
                true
            }
            false
        }

        searchEditTxt.setOnFocusChangeListener { view, hasFocus ->
            searchHistory.visibility =
                if (hasFocus && searchEditTxt.text.isEmpty() && searchHistoryTracks.isNotEmpty())
                    View.VISIBLE
                else
                    View.GONE
        }

        btnMainActivity.setOnClickListener {
            finish()
        }

        btnClearEditTxt.setOnClickListener {
            searchEditTxt.text.clear()
            playlistTracks.clear()
            playlistAdapter.notifyDataSetChanged()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchEditTxt.windowToken, 0)
            searchEditTxt.clearFocus()
        }

        btnClearSearchHistory.setOnClickListener {
            searchHistoryTracks.clear()
            searchHistoryAdapter.notifyDataSetChanged()
            searchHistory.visibility = View.GONE
            updateSearchHistoryPlaylistSharedPref(searchHistorySharedPrefPlaylist)
        }

        searchNewTrackListener = OnSharedPreferenceChangeListener { sharedPref, key ->
            if (key == SEARCH_HISTORY_NEW_TRACK) {
                val json = sharedPref.getString(SEARCH_HISTORY_NEW_TRACK, null)
                val track = createTrackFromJson(json)
                if (track != null) {
                    var copyIndex:Int? = null
                    searchHistoryTracks.forEachIndexed { index, historyTrack ->
                        if(historyTrack.trackId == track.trackId)
                            copyIndex = index
                    }

                    if(copyIndex != null){
                        searchHistoryTracks.removeAt(copyIndex!!)
                        searchHistoryAdapter.notifyItemRemoved(copyIndex!!)
                    }

                    searchHistoryTracks.add(0, track)
                    searchHistoryAdapter.notifyItemInserted(0)

                    if (searchHistoryTracks.size > 10) {
                        searchHistoryTracks.removeLast()
                        searchHistoryAdapter.notifyItemRemoved(10)
                    }
                }
                updateSearchHistoryPlaylistSharedPref(searchHistorySharedPrefPlaylist)
            }
        }

        searchHistorySharedPrefNewTrack.registerOnSharedPreferenceChangeListener(searchNewTrackListener)

        btnPlaceHolderUpdate.setOnClickListener {
            updateListTracks()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("SAVED_SEARCH_EDIT_TXT", savedSearchEditText)
        super.onSaveInstanceState(outState)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        hideErrorMessage()
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun updateListTracks() {

        val savedSearchTextCopy = savedSearchEditText

        if (!savedSearchTextCopy.isNullOrEmpty()) {
            playlistRetrofit.search(savedSearchTextCopy)
                .enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                        if (response.isSuccessful) {
                            playlistTracks.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                playlistTracks.addAll(response.body()!!.results)
                                playlistAdapter.notifyDataSetChanged()
                            }
                            if (playlistTracks.isEmpty())
                                showErrorMessage(
                                    getString(R.string.not_found),
                                    getDrawable(R.drawable.not_found),
                                    false
                                )
                            else {
                                hideErrorMessage()
                            }
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        showErrorMessage(
                            getString(R.string.failed_connection),
                            getDrawable(R.drawable.failed_connection),
                            true
                        )
                    }

                })
        }
    }

    private fun showErrorMessage(text: String, image: Drawable?, update: Boolean) {
        playlistTracks.clear()
        playlistAdapter.notifyDataSetChanged()
        Glide.with(this)
            .load(image)
            .transform(CenterCrop())
            .into(placeHolderImage)

        placeHolderMessage.text = text
        placeHolder.visibility = View.VISIBLE
        if (update)
            btnPlaceHolderUpdate.visibility = View.VISIBLE
        else
            btnPlaceHolderUpdate.visibility = View.GONE
    }

    private fun hideErrorMessage() {
        placeHolder.visibility = View.GONE
    }

    private fun updateSearchHistoryPlaylistSharedPref(searchHistorySharedPrefPlaylist: SharedPreferences){
        searchHistorySharedPrefPlaylist.edit()
            .putString(SEARCH_HISTORY_PLAYLIST, createJsonFromListTrack(searchHistoryTracks))
            .apply()
    }
}

fun createTrackFromJson(json: String?) = Gson().fromJson(json, Track::class.java)

private fun createListTrackFromJson(json: String?) =
    GsonBuilder().create().fromJson(json, object : TypeToken<MutableList<Track>>() {}.type) ?: mutableListOf<Track>()

private fun createJsonFromListTrack(listTrack: List<Track>) = Gson().toJson(listTrack)



