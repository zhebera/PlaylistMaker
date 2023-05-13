package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSearch = findViewById<Button>(R.id.btn_search)
        val btnLibrary = findViewById<Button>(R.id.btn_library)
        val btnSettings = findViewById<Button>(R.id.btn_settings)

        /** Обработка нажатия кнопок поиска и медиатеки двумя способами
        val btnSearchClickListener = object : View.OnClickListener{
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажата кнопка поиска", Toast.LENGTH_SHORT).show()
            }
        }

        btnSearch.setOnClickListener(btnSearchClickListener)

        btnLibrary.setOnClickListener{
            Toast.makeText(this@MainActivity, "Нажата кнопка медиатеки", Toast.LENGTH_SHORT).show()
        }*/

        btnSearch.setOnClickListener {
            val searchIntent = Intent(this, SettingsActivity::class.java)
            startActivity(searchIntent)
        }

        btnLibrary.setOnClickListener {
            val libraryIntent = Intent(this, LibraryActivity::class.java)
            startActivity(libraryIntent)
        }

        btnSettings.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }

    }
}