package com.example.playlistmaker

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

        val btnSearchClickListener = object : View.OnClickListener{
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажата кнопка поиска", Toast.LENGTH_SHORT).show()
            }
        }

        val btnLibraryClickListener = object : View.OnClickListener{
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажата кнопка медиатеки", Toast.LENGTH_SHORT).show()
            }
        }

        val btnSettingsClickListener = object : View.OnClickListener{
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity, "Нажата кнопка настроек", Toast.LENGTH_SHORT).show()
            }
        }

        btnSearch.setOnClickListener(btnSearchClickListener)
        btnLibrary.setOnClickListener(btnLibraryClickListener)
        btnSettings.setOnClickListener(btnSettingsClickListener)

        btnSearch.setOnClickListener{
            Toast.makeText(this@MainActivity, "Нажата кнопка поиска", Toast.LENGTH_SHORT).show()
        }

        btnLibrary.setOnClickListener{
            Toast.makeText(this@MainActivity, "Нажата кнопка медиатеки", Toast.LENGTH_SHORT).show()
        }

        btnSettings.setOnClickListener{
            Toast.makeText(this@MainActivity, "Нажата кнопка настроек", Toast.LENGTH_SHORT).show()
        }
    }
}