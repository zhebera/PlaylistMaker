package com.example.playlistmaker.player.ui

import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.models.Track
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.utils.createJsonFromTrack
import com.example.playlistmaker.utils.createTrackFromJson
import com.example.playlistmaker.databinding.ActivityAudioplayerBinding
import com.example.playlistmaker.utils.KEY_TRACK_ID
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.player.domain.presentation.PlayerViewModel
import com.example.playlistmaker.utils.dpToPx
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AudioplayerActivity : AppCompatActivity() {

    companion object {
        private const val SAVED_AUDIOPLAYER_STATE = "saved_audioplayer_state"
    }

    private lateinit var binding: ActivityAudioplayerBinding
    private lateinit var track: Track
    private lateinit var playButton: ImageButton
    private lateinit var timerTxt: TextView
    private lateinit var btnBack: ImageView
    private lateinit var placeholder: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var trackTime: TextView
    private lateinit var collectionName: TextView
    private lateinit var releaseDate: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView
    private lateinit var viewModel: PlayerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            track = createTrackFromJson(savedInstanceState.getString(SAVED_AUDIOPLAYER_STATE))
        }

        viewModel = ViewModelProvider(this, PlayerViewModel.getViewModelFactory())[PlayerViewModel::class.java]

        viewModel.playerState.observe(this){
            renderState(it)
        }

        viewModel.timing.observe(this){
            renderTimer(it)
        }

        viewModel.finishedPlay.observe(this){
            renderFinishPlay(it)
        }

        track = createTrackFromJson(intent.getStringExtra(KEY_TRACK_ID))
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeView()

        btnBack.setOnClickListener {
            finish()
        }

        viewModel.preparePlayer(track.previewUrl)

        playButton.setOnClickListener {
            viewModel.playControl()
        }
    }

    private fun initializeView() {
        btnBack = binding.btnBack
        placeholder = binding.placeholderPlayer
        trackName = binding.trackName
        artistName = binding.artistName
        trackTime = binding.trackTime
        collectionName = binding.collectionName
        releaseDate = binding.releaseDate
        primaryGenreName = binding.primaryGenreName
        country = binding.country
        timerTxt = binding.timerTxt
        playButton = binding.playButtonIb

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
    }
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SAVED_AUDIOPLAYER_STATE, createJsonFromTrack(track))
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
        onPlayerPauseView()
    }

    private fun renderState(state: PlayerState){
        when(state){
            PlayerState.STATE_PLAYING -> onPlayerStartView()
            PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> onPlayerPauseView()
            else -> Unit
        }
    }

    private fun renderTimer(timer: Int){
        timerTxt.text = SimpleDateFormat(("mm:ss"), Locale.getDefault()).format(timer)
    }

    private fun renderFinishPlay(finished: Boolean){
        if(finished){
            viewModel.finishPlay()
            playButton.setImageDrawable(getDrawable(R.drawable.play))
            timerTxt.text = getString(R.string.player_zero_timing)
        }
    }

    private fun onPlayerPauseView() {
        playButton.setImageDrawable(getDrawable(R.drawable.play))
    }

    private fun onPlayerStartView() {
        playButton.setImageDrawable(getDrawable(R.drawable.pause))
    }
}
