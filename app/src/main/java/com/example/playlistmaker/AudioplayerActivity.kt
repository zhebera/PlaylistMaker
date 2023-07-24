package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.*

class AudioplayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        val track = createTrackFromJson(Intent().getStringExtra(KEY_TRACK_ID))

        val btnBack = findViewById<ImageView>(R.id.btn_back)
        val placeholder = findViewById<ImageView>(R.id.placeholderPlayer)
        val trackName = findViewById<TextView>(R.id.trackName)
        val artistName = findViewById<TextView>(R.id.artistName)
        val trackTime = findViewById<TextView>(R.id.trackTime)
        val collectionName = findViewById<TextView>(R.id.collectionName)
        val releaseDate = findViewById<TextView>(R.id.releaseDate)
        val primaryGenreName = findViewById<TextView>(R.id.primaryGenreName)
        val country = findViewById<TextView>(R.id.country)

        btnBack.setOnClickListener {
            finish()
        }

        Glide.with(this)
             .load(track.artworkUrl100.replaceAfterLast('/',"512x512bb.jpg"))
             .placeholder(R.drawable.music_note)
             .centerCrop()
             .transform(RoundedCorners(dpToPx(8.0F,this)))
             .into(placeholder)

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        collectionName.text = track.collectionName
        releaseDate.text = track.releaseDate
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country
    }
}