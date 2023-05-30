package com.example.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView

class SearchActivity : AppCompatActivity() {
    companion object{
        var SAVED_SEARCH_EDIT_TXT: String? = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        if (savedInstanceState != null){
            SAVED_SEARCH_EDIT_TXT = savedInstanceState.getString("SAVED_SEARCH_EDIT_TXT")
        }

        val searchEditTxt = findViewById<EditText>(R.id.inputEditText)
        val btnMainActivity = findViewById<ImageView>(R.id.btn_main_activity)
        val btnClearEditTxt = findViewById<ImageView>(R.id.clearIcon)

        val textWatcher = object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                btnClearEditTxt.visibility = clearButtonVisibility(s)
                SAVED_SEARCH_EDIT_TXT = searchEditTxt.text.toString()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }

        searchEditTxt.setText(SAVED_SEARCH_EDIT_TXT)
        searchEditTxt.addTextChangedListener(textWatcher)

        btnMainActivity.setOnClickListener {
            finish()
        }

        btnClearEditTxt.setOnClickListener {
            searchEditTxt.text.clear()
            val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(searchEditTxt.windowToken, 0)
            searchEditTxt.clearFocus()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("SAVED_SEARCH_EDIT_TXT", SAVED_SEARCH_EDIT_TXT)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }
}



