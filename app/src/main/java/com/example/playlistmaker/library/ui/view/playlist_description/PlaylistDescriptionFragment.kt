package com.example.playlistmaker.library.ui.view.playlist_description

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistDescriptionBinding
import com.example.playlistmaker.library.domain.models.LibraryTrackState
import com.example.playlistmaker.library.ui.viewmodel.playlist_description.PlaylistDescriptionViewModel
import com.example.playlistmaker.models.Playlist
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.search.ui.view.PlaylistSearchAdapter
import com.example.playlistmaker.utils.*
import com.example.playlistmaker.utils.converters.createJsonFromPlaylist
import com.example.playlistmaker.utils.converters.createPlaylistFromJson
import com.example.playlistmaker.utils.converters.rightEndingMinutes
import com.example.playlistmaker.utils.converters.rightEndingTrack
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

class PlaylistDescriptionFragment : Fragment() {

    private var _binding: FragmentPlaylistDescriptionBinding? = null
    private val binding: FragmentPlaylistDescriptionBinding
        get() = _binding!!

    private lateinit var playlist: Playlist
    private var adapter: PlaylistSearchAdapter? = null
    private lateinit var bottomSheetSettingsBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private val viewModel by viewModel<PlaylistDescriptionViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPlaylistDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, true) { track ->
            val bundle = bundleOf(KEY_TRACK_ID to createJsonFromTrack(track))
            findNavController().navigate(R.id.action_playlistDescriptionFragment_to_audioplayerFragment, bundle)
        }

        playlist = createPlaylistFromJson(requireArguments().getString(PLAYLIST_ID))

        adapter = PlaylistSearchAdapter(object : PlaylistSearchAdapter.SearchClickListener {
            override fun onTrackClick(track: Track) {
                onTrackClickDebounce(track)
            }

            override fun onTrackLongClick(track: Track) {
                showDialogDeleteTrack(track)
            }
        })

        binding.rvTracks.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvTracks.adapter = adapter

        viewModel.apply {
            playlistTracks.observe(viewLifecycleOwner, ::renderState)
            playlist.observe(viewLifecycleOwner, ::updatePlaylist)
        }

        binding.ivBtnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.ivShare.setOnClickListener {
            sharePlaylist()
        }

        binding.ivSettings.setOnClickListener {
            showBottomSheetSettings()
        }

        binding.tvShare.setOnClickListener {
            sharePlaylist()
            bottomSheetSettingsBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.tvDeletePlaylist.setOnClickListener {
            showDialogDeletePlaylist()
        }

        binding.tvEditInformation.setOnClickListener {
            findNavController().navigate(
                R.id.action_playlistDescriptionFragment_to_playlistEditFragment,
                bundleOf(PLAYLIST_ID to createJsonFromPlaylist(playlist))
            )
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })
    }

    private fun initView() {
        binding.tvPlaylistName.text = playlist.name
        binding.tvPlaylistOverview.text = playlist.overview
        updateCountSongs()
        updateSumTime()
        Glide.with(requireContext())
            .load(viewModel.getImage(playlist.imageName))
            .placeholder(R.drawable.music_note)
            .into(binding.ivPlaceholder)

        bottomSheetSettingsBehavior = BottomSheetBehavior.from(binding.bottomSheetSettings)
        bottomSheetSettingsBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    private fun updatePlaylist(playlistUpdate: Playlist){
        playlist = playlistUpdate
        initView()
    }

    private fun updateSumTime() {
        val sumDuration = if (adapter?.tracks?.isNullOrEmpty() == false)
            adapter!!.tracks.sumOf { it.trackTimeMillis }
        else
            0
        binding.tvMinutes.text = rightEndingMinutes(
            TimeUnit.MILLISECONDS.toMinutes(sumDuration).toInt()
        )
    }

    private fun updateCountSongs(){
        binding.tvCount.text = rightEndingTrack(adapter!!.itemCount)
    }

    private fun renderState(state: LibraryTrackState) {
        when (state) {
            is LibraryTrackState.Content -> showData(state.data)
            is LibraryTrackState.Empty -> showEmpty()
        }
    }

    private fun showData(listTrack: List<Track>) {
        adapter?.apply {
            tracks.clear()
            tracks.addAll(listTrack)
            notifyDataSetChanged()
        }
        updateSumTime()
        updateCountSongs()
    }

    private fun showEmpty() {
        adapter?.apply {
            tracks.clear()
            notifyDataSetChanged()
        }
        updateSumTime()
        updateCountSongs()
    }

    private fun showDialogDeleteTrack(track: Track) {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.DialogButtons)
            .setTitle("Удалить трек")
            .setMessage("Вы уверены, что хотите удалить трек из плейлиста?")
            .setNegativeButton("Отмена") { dialog, which ->
            }
            .setPositiveButton("Удалить") { dialog, which ->
                viewModel.deleteTrackFromPlaylist(playlist.id, track)
            }
        dialog.show()
    }

    private fun showDialogDeletePlaylist() {
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.DialogButtons)
            .setTitle("Хотите удалить плейлист <<${playlist.name}>>?")
            .setNegativeButton("Нет") { dialog, which ->
            }
            .setPositiveButton("Да") { dialog, which ->
                viewModel.deletePlaylist(playlist.id)
                findNavController().popBackStack()
            }
        dialog.show()
    }

    private fun sharePlaylist() {
        adapter?.apply {
            if (tracks.isNullOrEmpty())
                Toast.makeText(requireContext(), getString(R.string.no_tracks_for_sharing), Toast.LENGTH_SHORT).show()
            else
                shareTracks()
        }
    }

    private fun shareTracks() {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, getPlaylistInfo())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(intent, null)
        startActivity(shareIntent)
    }

    private fun getPlaylistInfo(): String {
        return "${playlist.name}\n" +
                "${playlist.overview}\n" +
                "${playlist.tracks.size}\n" +
                getTracksInfo()
    }

    private fun getTracksInfo(): String {
        val tracksInfo = adapter!!.tracks.mapIndexed { index, track ->
            "${index + 1}.${track.artistName} - ${track.trackName}(${
                SimpleDateFormat(
                    "mm:ss",
                    Locale.getDefault()
                ).format(track.trackTimeMillis)
            })"
        }
        return tracksInfo.stream().collect(Collectors.joining("\n"))
    }

    private fun showBottomSheetSettings() {
        bottomSheetSettingsBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        Glide.with(requireContext())
            .load(viewModel.getImage(playlist.imageName))
            .placeholder(R.drawable.music_note)
            .into(binding.playlistLayout.ivPlaylist)
        binding.playlistLayout.tvPlaylistName.text = playlist.name
        binding.playlistLayout.tvCountSongs.text = rightEndingTrack(playlist.tracks.size)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getTracks(playlist.id)
        viewModel.updatePlaylist(playlist.id)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}