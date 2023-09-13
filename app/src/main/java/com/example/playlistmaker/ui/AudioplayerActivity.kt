package com.example.playlistmaker.ui

import com.example.playlistmaker.domain.models.Track
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.utils.createJsonFromTrack
import com.example.playlistmaker.utils.createTrackFromJson
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.dpToPx
import com.example.playlistmaker.utils.consts.KEY_TRACK_ID
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AudioplayerActivity : AppCompatActivity() {

    companion object {
        private const val SAVED_AUDIOPLAYER_STATE = "saved_audioplayer_state"
        private const val REFRESH_TIMER_MILLIS = 300L
    }

    private lateinit var track: Track
    private lateinit var playButton: ImageButton
    private lateinit var timerTxt: TextView

    private var handler: Handler? = null
    private val timer = setTimer()
    private val mediaPlayer = Creator.providePlayerInteractor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audioplayer)

        if (savedInstanceState != null) {
            track = createTrackFromJson(savedInstanceState.getString(SAVED_AUDIOPLAYER_STATE))
        }

        track = createTrackFromJson(intent.getStringExtra(KEY_TRACK_ID))

        val btnBack = findViewById<ImageView>(R.id.btn_back)
        val placeholder = findViewById<ImageView>(R.id.placeholderPlayer)
        val trackName = findViewById<TextView>(R.id.trackName)
        val artistName = findViewById<TextView>(R.id.artistName)
        val trackTime = findViewById<TextView>(R.id.trackTime)
        val collectionName = findViewById<TextView>(R.id.collectionName)
        val releaseDate = findViewById<TextView>(R.id.releaseDate)
        val primaryGenreName = findViewById<TextView>(R.id.primaryGenreName)
        val country = findViewById<TextView>(R.id.country)
        timerTxt = findViewById(R.id.timerTxt)
        playButton = findViewById(R.id.playButtonIb)
        handler = Handler(Looper.getMainLooper())

        btnBack.setOnClickListener {
            finish()
        }

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.music_note)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8.0F, this)))
            .into(placeholder)

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text =
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
        collectionName.text = track.collectionName
        releaseDate.text = LocalDateTime.parse(
            track.releaseDate,
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        ).year.toString()
        primaryGenreName.text = track.primaryGenreName
        country.text = track.country

        mediaPlayer.preparePlayer(
            track.previewUrl
        ) {
            playButton.setImageDrawable(getDrawable(R.drawable.play))
            handler?.removeCallbacks(timer)
            timerTxt.text = "00:00"
        }

        playButton.setOnClickListener {
            mediaPlayer.playControl(
                {
                    onPlayerStartView()
                },
                {
                    onPlayerPauseView()
                }
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SAVED_AUDIOPLAYER_STATE, createJsonFromTrack(track))
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.pausePlayer{
            onPlayerPauseView()
        }
    }

    private fun onPlayerPauseView(){
        handler?.removeCallbacks(timer)
        playButton.setImageDrawable(getDrawable(R.drawable.play))
    }

    private fun onPlayerStartView(){
        handler?.post(timer)
        playButton.setImageDrawable(getDrawable(R.drawable.pause))
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun setTimer(): Runnable {
        return object : Runnable {
            override fun run() {
                timerTxt.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.getCurrentPosition())

                handler?.postDelayed(
                    this,
                    REFRESH_TIMER_MILLIS
                )
            }
        }
    }
}
