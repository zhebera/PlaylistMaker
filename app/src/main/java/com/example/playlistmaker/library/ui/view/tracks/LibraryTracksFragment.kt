package com.example.playlistmaker.library.ui.view.tracks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentMediatekaTracksBinding
import com.example.playlistmaker.library.domain.models.LibraryTrackState
import com.example.playlistmaker.library.ui.viewmodel.tracks.LibraryTracksViewModel
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.search.ui.view.PlaylistSearchAdapter
import com.example.playlistmaker.utils.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.utils.KEY_TRACK_ID
import com.example.playlistmaker.utils.createJsonFromTrack
import com.example.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryTracksFragment : Fragment() {

    private var _binding: FragmentMediatekaTracksBinding? = null
    private val binding: FragmentMediatekaTracksBinding
        get() = _binding!!

    private var adapter: PlaylistSearchAdapter? = null
    private val viewModel by viewModel<LibraryTracksViewModel>()
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMediatekaTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            val bundle = bundleOf(KEY_TRACK_ID to createJsonFromTrack(track))
            findNavController().navigate(R.id.action_libraryFragment_to_audioplayerFragment, bundle)
        }

        adapter = PlaylistSearchAdapter(object: PlaylistSearchAdapter.SearchClickListener{
            override fun onTrackClick(track: Track) {
                onTrackClickDebounce(track)
            }

            override fun onTrackLongClick(track: Track) = Unit
        })

        binding.rvLibrary.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvLibrary.adapter = adapter

        viewModel.libraryTracks.observe(viewLifecycleOwner, ::renderState)
    }

    override fun onResume() {
        super.onResume()
        viewModel.getData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun renderState(state: LibraryTrackState) {
        when (state) {
            is LibraryTrackState.Content -> showContent(state.data)
            is LibraryTrackState.Empty -> showEmpty()
        }
    }

    private fun showEmpty() {
        binding.rvLibrary.isVisible = false
        binding.llPlaceHolder.isVisible = true
    }

    private fun showContent(tracks: List<Track>) {
        binding.rvLibrary.isVisible = true
        binding.llPlaceHolder.isVisible = false

        adapter?.tracks?.clear()
        adapter?.tracks?.addAll(tracks)
        adapter?.notifyDataSetChanged()
    }

    companion object {
        fun newInstance() = LibraryTracksFragment()
    }
}