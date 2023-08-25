package com.example.playlistmaker

import Track
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

const val KEY_TRACK_ID = "key_track_id"

class AudioplayerActivity : AppCompatActivity() {

    enum class State{
        STATE_DEFAULT,
        STATE_PREPARED,
        STATE_PLAYING,
        STATE_PAUSED
    }

    companion object {
        private const val SAVED_AUDIOPLAYER_STATE = "saved_audioplayer_state"

        private const val REFRESH_TIMER_MILLIS = 300L
    }

    private lateinit var track: Track
    private lateinit var playButton: ImageButton
    private lateinit var timerTxt: TextView
    private val mediaPlayer = MediaPlayer()
    private var playerState = State.STATE_DEFAULT
    private var handler: Handler? = null
    private var currentPosition = 0
    private val timer = setTimer()

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

        preparePlayer()

        playButton.setOnClickListener {
            playControl()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SAVED_AUDIOPLAYER_STATE, createJsonFromTrack(track))
        super.onSaveInstanceState(outState)
    }

    private fun playControl() {
        when(playerState) {
            State.STATE_PLAYING -> {
                pausePlayer()
            }

            State.STATE_PREPARED, State.STATE_PAUSED -> {
                startPlayer()
            }

            else -> {}
        }
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(track.previewUrl)
        mediaPlayer.prepareAsync()
        currentPosition = mediaPlayer.currentPosition
        mediaPlayer.setOnPreparedListener {
            playerState = State.STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageDrawable(getDrawable(R.drawable.play))
            playerState = State.STATE_PREPARED
            handler?.removeCallbacks(timer)
            timerTxt.text = "00:00"
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        handler?.post(timer)
        playButton.setImageDrawable(getDrawable(R.drawable.pause))
        playerState = State.STATE_PLAYING
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        handler?.removeCallbacks(timer)
        playButton.setImageDrawable(getDrawable(R.drawable.play))
        playerState = State.STATE_PAUSED
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun setTimer(): Runnable{
        return object: Runnable{
            override fun run() {
                timerTxt.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)

                handler?.postDelayed(
                    this,
                    REFRESH_TIMER_MILLIS
                )
            }
        }
    }
}
