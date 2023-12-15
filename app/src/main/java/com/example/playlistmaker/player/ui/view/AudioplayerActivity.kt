package com.example.playlistmaker.player.ui.view

import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.models.Track
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityAudioplayerBinding
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.models.PlaylistState
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AudioplayerActivity : AppCompatActivity() {

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
    private lateinit var favouriteButton: ImageView
    private lateinit var addPlaylistBtn: ImageView
    private lateinit var bottomSheet: ConstraintLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private var playlistRecyclerView: RecyclerView? = null
    private var playlistAdapter: PlaylistAdapter? = null
    private lateinit var onPlaylstClickDebounce: (Playlist) -> Unit
    private val viewModel by viewModel<PlayerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioplayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onPlaylstClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, lifecycleScope, false){playlist ->
            TODO()
        }

        playlistAdapter = PlaylistAdapter{playlist ->
            onPlaylstClickDebounce(playlist)
        }

        var preparedTrack = false

        if (savedInstanceState != null) {
            track = createTrackFromJson(savedInstanceState.getString(SAVED_AUDIOPLAYER_STATE))
            preparedTrack = savedInstanceState.getBoolean(PREPARED_TRACK)
        }

        viewModel.playerState.observe(this){
            renderState(it)
        }

        viewModel.isFavorite.observe(this){
            renderFavorite(it)
        }

        viewModel.playlistState.observe(this){
            renderPlaylistState(it)
        }

        track = createTrackFromJson(intent.getStringExtra(KEY_TRACK_ID))

        initializeView()

        playlistRecyclerView?.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        playlistRecyclerView?.adapter = playlistAdapter

        btnBack.setOnClickListener {
            finish()
        }

        if(!preparedTrack)
            viewModel.preparePlayer(track.previewUrl)

        playButton.setOnClickListener {
            viewModel.playControl()
        }

        favouriteButton.setOnClickListener {
            viewModel.changeFavourite(track)
        }

        addPlaylistBtn.setOnClickListener {
            addTrackToPlaylist()
        }
    }

    private fun renderFavorite(favorite: Boolean) {
        if(favorite)
            favouriteButton.setImageDrawable(getDrawable(R.drawable.like))
        else
            favouriteButton.setImageDrawable(getDrawable(R.drawable.no_like))
    }

    private fun initializeView() {
        btnBack = binding.ivBtnBack
        placeholder = binding.ivPlaceholderPlayer
        trackName = binding.tvTrackName
        artistName = binding.tvArtistName
        trackTime = binding.tvTrackTime
        collectionName = binding.tvCollectionName
        releaseDate = binding.tvReleaseDate
        primaryGenreName = binding.tvPrimaryGenreName
        country = binding.tvCountry
        timerTxt = binding.tvTimer
        playButton = binding.ibPlayButton
        favouriteButton = binding.ibFavourite
        bottomSheet = binding.bottomSheet
        addPlaylistBtn = binding.ibAddPlaylistBtn
        playlistRecyclerView = binding.rvPlaylist

        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

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
        viewModel.checkFavorite(track.trackId)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SAVED_AUDIOPLAYER_STATE, createJsonFromTrack(track))
        outState.putBoolean(PREPARED_TRACK, true)
        super.onSaveInstanceState(outState)
    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    private fun renderState(state: PlayerState){
        when(state){
            is PlayerState.Playing -> onPlayerStartView(state.progress)
            is PlayerState.Prepared, is PlayerState.Paused -> onPlayerPauseView(state.progress)
            else -> Unit
        }
    }

    private fun onPlayerPauseView(timer: String) {
        timerTxt.text = timer
        playButton.setImageDrawable(getDrawable(R.drawable.play))
    }

    private fun onPlayerStartView(timer: String) {
        timerTxt.text = timer
        playButton.setImageDrawable(getDrawable(R.drawable.pause))
    }

    private fun addTrackToPlaylist(){
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun renderPlaylistState(playlistState: PlaylistState){
        when(playlistState){
            is PlaylistState.Content -> showPlaylists(playlistState.data)
            is PlaylistState.Empty -> Unit
        }
    }

    private fun showPlaylists(listPlaylist: List<Playlist>){

        playlistAdapter?.playlists?.clear()
        playlistAdapter?.playlists?.addAll(listPlaylist)
        playlistAdapter?.notifyDataSetChanged()
    }

    companion object {
        private const val SAVED_AUDIOPLAYER_STATE = "saved_audioplayer_state"
        private const val PREPARED_TRACK = "prepared_track"
    }
}
