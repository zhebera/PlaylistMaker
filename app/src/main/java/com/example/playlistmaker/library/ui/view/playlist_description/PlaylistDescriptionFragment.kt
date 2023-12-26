package com.example.playlistmaker.library.ui.view.playlist_description

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
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
import com.example.playlistmaker.utils.PLAYLIST_ID
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistDescriptionFragment: Fragment() {

    private var _binding: FragmentPlaylistDescriptionBinding? = null
    private val binding: FragmentPlaylistDescriptionBinding
        get() = _binding!!

    private var playlistId: Long? = null
    private var playlist: Playlist? = null
    private val viewModel by viewModel<PlaylistDescriptionViewModel>()
    private var adapter: PlaylistSearchAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPlaylistDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistId = requireArguments().getLong(PLAYLIST_ID)
        playlist = viewModel.getPlaylist(playlistId!!)

        adapter = PlaylistSearchAdapter{

        }

        initView()

        binding.rvTracks.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvTracks.adapter = adapter

        viewModel.playlistTracks.observe(viewLifecycleOwner){
            renderState(it)
        }

        binding.ivBtnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })
    }

    private fun initView(){
        binding.tvPlaylistName.text = playlist?.name
        binding.tvPlaylistOverview.text = playlist?.overview
        Glide.with(requireContext())
            .load(viewModel.getImage(playlist?.imageName))
            .placeholder(R.drawable.music_note)
            .into(binding.ivPlaceholder)
    }

    private fun renderState(state: LibraryTrackState){
        when(state){
            is LibraryTrackState.Content -> showData(state.data)
            is LibraryTrackState.Empty -> showEmpty()
        }
    }

    private fun showData(listTrack: List<Track>){
        adapter?.apply {
            tracks.clear()
            tracks.addAll(listTrack)
            notifyDataSetChanged()
        }
    }

    private fun showEmpty(){
        adapter?.apply {
            tracks.clear()
            notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        playlistId?.let { viewModel.getTracks(it) }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}