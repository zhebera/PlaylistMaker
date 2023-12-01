package com.example.playlistmaker.library.ui.view.tracks

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.FragmentMediatekaTracksBinding
import com.example.playlistmaker.library.domain.models.LibraryTrackState
import com.example.playlistmaker.library.ui.viewmodel.tracks.LibraryTracksViewModel
import com.example.playlistmaker.models.Track
import com.example.playlistmaker.player.ui.view.AudioplayerActivity
import com.example.playlistmaker.search.ui.view.PlaylistAdapter
import com.example.playlistmaker.utils.CLICK_DEBOUNCE_DELAY
import com.example.playlistmaker.utils.KEY_TRACK_ID
import com.example.playlistmaker.utils.createJsonFromTrack
import com.example.playlistmaker.utils.debounce
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryTracksFragment: Fragment() {

    private lateinit var _binding: FragmentMediatekaTracksBinding
    private val binding: FragmentMediatekaTracksBinding
        get() = _binding

    private var libraryRecyclerView: RecyclerView? = null
    private var placeHolderLinearLayout: LinearLayout? = null
    private var placeHolderImageView: ImageView? = null
    private var placeholderTextView: TextView? = null
    private var adapter: PlaylistAdapter? = null
    private val viewModel by viewModel<LibraryTracksViewModel>()
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMediatekaTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeView()

        onTrackClickDebounce = debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            val audioplayerIntent = Intent(requireContext(), AudioplayerActivity::class.java)
            audioplayerIntent.putExtra(KEY_TRACK_ID, createJsonFromTrack(track))
            startActivity(audioplayerIntent)
        }

        adapter = PlaylistAdapter{track ->
            onTrackClickDebounce(track)
        }

        libraryRecyclerView?.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        libraryRecyclerView?.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.libraryTracks.collect{ state ->
                renderState(state)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getData()
    }

    private fun initializeView(){
        libraryRecyclerView = binding.rvLibrary
        placeHolderLinearLayout = binding.llPlaceHolder
        placeHolderImageView = binding.ivPlaceHolder
        placeholderTextView = binding.tvPlaceholderMessage
    }

    private fun renderState(state: LibraryTrackState) {
        when (state) {
            is LibraryTrackState.Content -> showContent(state.data)
            is LibraryTrackState.Empty -> showEmpty()
            else -> Unit
        }
    }

    private fun showEmpty() {
        libraryRecyclerView?.visibility = View.GONE
        placeHolderLinearLayout?.visibility = View.VISIBLE
    }

    private fun showContent(tracks: List<Track>){
        libraryRecyclerView?.visibility = View.VISIBLE
        placeHolderLinearLayout?.visibility = View.GONE

        adapter?.tracks?.clear()
        adapter?.tracks?.addAll(tracks.reversed())
        adapter?.notifyDataSetChanged()
    }

    companion object{
        fun newInstance() = LibraryTracksFragment()
    }
}