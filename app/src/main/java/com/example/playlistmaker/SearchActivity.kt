package com.example.playlistmaker

import Track
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {
    companion object{
        private const val BASE_URL = "https://itunes.apple.com"
    }

    private var savedSearchEditText: String? = null
    private val adapter = PlaylistAdapter()
    private val listTracks = mutableListOf<Track>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        if (savedInstanceState != null){
            savedSearchEditText = savedInstanceState.getString("SAVED_SEARCH_EDIT_TXT")
        }

        val searchEditTxt = findViewById<EditText>(R.id.inputEditText)
        val btnMainActivity = findViewById<ImageView>(R.id.btn_main_activity)
        val btnClearEditTxt = findViewById<ImageView>(R.id.clearIcon)
        val trackRecyclerView = findViewById<RecyclerView>(R.id.trackRecyclerView)

        val playlistRetrofit = PlaylistRetrofit(BASE_URL).playlistRetrofit

        val textWatcher = object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnClearEditTxt.visibility = clearButtonVisibility(s)
                savedSearchEditText = searchEditTxt.text.toString()
            }

            override fun afterTextChanged(s: Editable?) = Unit
        }

        adapter.listTrack = listTracks
        trackRecyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        trackRecyclerView.adapter = adapter


        searchEditTxt.setText(savedSearchEditText)
        searchEditTxt.addTextChangedListener(textWatcher)
        searchEditTxt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if(searchEditTxt.text.isNotEmpty()){
                    playlistRetrofit.search(searchEditTxt.text.toString())
                        .enqueue(object: Callback<TrackResponse>{
                            override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                                if(response.isSuccessful){
                                    listTracks.clear()
                                    if(response.body()!!.results.isNotEmpty()){
                                        listTracks.addAll(response.body()!!.results)
                                        adapter.notifyDataSetChanged()
                                    }
                                    if(listTracks.isEmpty())
                                        showMessage(getString(R.string.not_found), getDrawable(R.drawable.not_found))
                                    else{
                                      showMessage("",null)
                                    }
                                }
                            }

                            override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                                showMessage(getString(R.string.failed_connection), getDrawable(R.drawable.failed_connection))
                            }

                        })
                }
                true
            }
            false
        }

        btnMainActivity.setOnClickListener {
            finish()
        }

        btnClearEditTxt.setOnClickListener {
            searchEditTxt.text.clear()
            listTracks.clear()
            adapter.notifyDataSetChanged()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchEditTxt.windowToken, 0)
            searchEditTxt.clearFocus()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SAVED_SEARCH_EDIT_TXT", savedSearchEditText)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun showMessage(text: String, image: Drawable?){

        val placeHolder = findViewById<LinearLayout>(R.id.placeHolder)
        val placeHolderImage = findViewById<ImageView>(R.id.placeHolderImage)
        val placeHolderMessage = findViewById<TextView>(R.id.placeHolderMessage)

        if(text.isEmpty()){
            placeHolder.visibility = View.GONE
        }
        else{
            listTracks.clear()
            adapter.notifyDataSetChanged()
            Glide.with(this)
                .load(image)
                .transform(CenterCrop())
                .into(placeHolderImage)

            placeHolderMessage.text = text
            placeHolder.visibility = View.VISIBLE


        }
    }
}



