package com.example.playlistmaker.player.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentAudioplayerBinding
import com.example.playlistmaker.library.ui.view.playlist.PlaylistAdapter
import com.example.playlistmaker.models.Playlist
import com.example.playlistmaker.models.PlaylistState
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.player.domain.models.PlayerState
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class AudioplayerFragment : Fragment() {

    private var _binding: FragmentAudioplayerBinding? = null
    private val binding: FragmentAudioplayerBinding
        get() = _binding!!

    private lateinit var track: Track

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit
    private val playlistAdapter = PlaylistAdapter(R.layout.playlist) { playlist ->
        onPlaylistClickDebounce(playlist)
    }
    private var preparedTrack = false
    private val viewModel by viewModel<PlayerViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentAudioplayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState != null) {
            track = createTrackFromJson(savedInstanceState.getString(SAVED_AUDIOPLAYER_STATE))
            preparedTrack = savedInstanceState.getBoolean(PREPARED_TRACK)
        }

        onPlaylistClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, lifecycleScope, false) { playlist ->
            viewModel.addTrackToPlaylist(playlist, track)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        viewModel.apply {
            playerState.observe(viewLifecycleOwner, ::renderState)
            isFavorite.observe(viewLifecycleOwner, ::renderFavorite)
            playlistState.observe(viewLifecycleOwner, ::renderPlaylistState)
            showToast.observe(viewLifecycleOwner, ::showToast)
        }

        track = createTrackFromJson(requireArguments().getString(KEY_TRACK_ID))

        initializeView()

        binding.rvPlaylist.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvPlaylist.adapter = playlistAdapter

        binding.ivBtnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        if (!preparedTrack)
            viewModel.preparePlayer(track.previewUrl)

        binding.ibPlayButton.setOnClickListener {
            viewModel.playControl()
        }

        binding.ibFavourite.setOnClickListener {
            viewModel.changeFavourite(track)
        }

        binding.ibAddPlaylistBtn.setOnClickListener {
            viewModel.getAllPlaylist()
            addTrackToPlaylist()
        }

        binding.btnPlaylistCreate.setOnClickListener {
            preparedTrack = true
            findNavController().navigate(R.id.action_audioplayerFragment_to_playlistCreateFragment)
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.overlay.isVisible = false
                    }

                    else -> {
                        binding.overlay.isVisible = true
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) = Unit
        })

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })
    }

    private fun renderFavorite(favorite: Boolean) {
        if (favorite)
            binding.ibFavourite.setImageDrawable(requireContext().getDrawable(R.drawable.like))
        else
            binding.ibFavourite.setImageDrawable(requireContext().getDrawable(R.drawable.no_like))
    }

    private fun initializeView() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        Glide.with(this)
            .load(track.artworkUrl100.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.music_note)
            .centerCrop()
            .transform(RoundedCorners(dpToPx(8.0F, requireContext())))
            .into(binding.ivPlaceholderPlayer)

        with(binding){
            tvTrackName.text = track.trackName
            tvArtistName.text = track.artistName
            tvTrackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            tvCollectionName.text = track.collectionName
            tvReleaseDate.text = LocalDateTime.parse(
                track.releaseDate,
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            ).year.toString()
            tvPrimaryGenreName.text = track.primaryGenreName
            tvCountry.text = track.country
        }

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

    private fun renderState(state: PlayerState) {
        when (state) {
            is PlayerState.Playing -> onPlayerStartView(state.progress)
            is PlayerState.Prepared, is PlayerState.Paused -> onPlayerPauseView(state.progress)
            else -> Unit
        }
    }

    private fun onPlayerPauseView(timer: String) {
        binding.tvTimer.text = timer
        binding.ibPlayButton.setImageDrawable(requireContext().getDrawable(R.drawable.play))
    }

    private fun onPlayerStartView(timer: String) {
        binding.tvTimer.text = timer
        binding.ibPlayButton.setImageDrawable(requireContext().getDrawable(R.drawable.pause))
    }

    private fun addTrackToPlaylist() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun renderPlaylistState(playlistState: PlaylistState) {
        when (playlistState) {
            is PlaylistState.Content -> showPlaylists(playlistState.data)
            is PlaylistState.Empty -> Unit
        }
    }

    private fun showPlaylists(listPlaylist: List<Playlist>) {
        with(playlistAdapter) {
            playlists.clear()
            playlists.addAll(listPlaylist)
            notifyDataSetChanged()
        }
    }

    private fun showToast(message: String?) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    companion object {
        private const val SAVED_AUDIOPLAYER_STATE = "saved_audioplayer_state"
        private const val PREPARED_TRACK = "prepared_track"
    }
}
