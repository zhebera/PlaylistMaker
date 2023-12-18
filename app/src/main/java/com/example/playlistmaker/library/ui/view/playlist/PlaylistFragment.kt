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
import com.example.playlistmaker.models.Playlist
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
    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit
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

        onPlaylistClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, lifecycleScope, false){ playlist ->

        }

        playlistAdapter = PlaylistAdapter(R.layout.playlist_grid){playlist ->
            onPlaylistClickDebounce(playlist)
        }

        binding.rvPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvPlaylists.adapter = playlistAdapter

        viewModel.libraryPlaylist.observe(viewLifecycleOwner){
            renderPlaylistState(it)
        }

        binding.btnPlaylistCreate.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_playlistCreateFragment)
        }
    }

    private fun renderPlaylistState(playlistState: PlaylistState){
        when(playlistState){
            is PlaylistState.Content -> showPlaylists(playlistState.data)
            is PlaylistState.Empty -> showEmpty()
        }
    }

    private fun showPlaylists(listPlaylist: List<Playlist>){
        binding.flPlaceholder.visibility = View.GONE
        binding.rvPlaylists.visibility = View.VISIBLE

        playlistAdapter?.playlists?.clear()
        playlistAdapter?.playlists?.addAll(listPlaylist)
        playlistAdapter?.notifyDataSetChanged()
    }

    private fun showEmpty(){
        binding.flPlaceholder.visibility = View.VISIBLE
        binding.rvPlaylists.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        viewModel.getPlaylists()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object{
        fun newInstance() = PlaylistFragment()
    }
}