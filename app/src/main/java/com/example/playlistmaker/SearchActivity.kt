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
import android.widget.*
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

    private lateinit var searchEditTxt: EditText
    private lateinit var btnPlaceHolderUpdate: Button
    private lateinit var placeHolder: LinearLayout
    private lateinit var placeHolderImage: ImageView
    private lateinit var placeHolderMessage: TextView

    private var savedSearchEditText: String? = null
    private val adapter = PlaylistAdapter()
    private val listTracks = mutableListOf<Track>()
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


        if (savedInstanceState != null){
            savedSearchEditText = savedInstanceState.getString("SAVED_SEARCH_EDIT_TXT")
        }

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
                updateListTracks()
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

        btnPlaceHolderUpdate.setOnClickListener {
            updateListTracks()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SAVED_SEARCH_EDIT_TXT", savedSearchEditText)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        showMessage("",null, false)
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun updateListTracks(){

        if(!savedSearchEditText.isNullOrEmpty()){
            playlistRetrofit.search(savedSearchEditText!!)
                .enqueue(object: Callback<TrackResponse>{
                    override fun onResponse(call: Call<TrackResponse>, response: Response<TrackResponse>) {
                        if(response.isSuccessful){
                            listTracks.clear()
                            if(response.body()!!.results.isNotEmpty()){
                                listTracks.addAll(response.body()!!.results)
                                adapter.notifyDataSetChanged()
                            }
                            if(listTracks.isEmpty())
                                showMessage(getString(R.string.not_found), getDrawable(R.drawable.not_found), false)
                            else{
                                showMessage("",null, false)
                            }
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        showMessage(getString(R.string.failed_connection), getDrawable(R.drawable.failed_connection), true)
                    }

                })
        }
    }

    private fun showMessage(text: String, image: Drawable?, update: Boolean){

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
            if(update)
                btnPlaceHolderUpdate.visibility = View.VISIBLE
            else
                btnPlaceHolderUpdate.visibility = View.GONE
        }
    }
}



