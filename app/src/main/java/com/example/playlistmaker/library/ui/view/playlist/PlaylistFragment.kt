package com.example.playlistmaker.library.ui.view.playlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.library.domain.models.Playlist
import com.example.playlistmaker.library.ui.viewmodel.playlist.PlaylistViewModel
import com.example.playlistmaker.models.PlaylistState
import com.example.playlistmaker.player.ui.view.PlaylistAdapter
import com.example.playlistmaker.utils.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistFragment: Fragment() {

    private var _binding: FragmentPlaylistBinding? = null
    private val binding: FragmentPlaylistBinding
        get() = _binding!!
    private var playlistAdapter: PlaylistAdapter? = null
    private lateinit var onPlaylstClickDebounce: (Playlist) -> Unit
    private val viewModel by viewModel<PlaylistViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onPlaylstClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, lifecycleScope, false){ playlist ->
            TODO()
        }

        playlistAdapter = PlaylistAdapter{playlist ->
            onPlaylstClickDebounce(playlist)
        }

        binding.rvPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvPlaylists.adapter = playlistAdapter

        viewModel.libraryPlaylist.observe(viewLifecycleOwner){
            renderPlaylistState(it)
        }

        binding.btnPlaceholderCreate.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_playlistCreateFragment)
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        fun newInstance() = PlaylistFragment()
    }
}