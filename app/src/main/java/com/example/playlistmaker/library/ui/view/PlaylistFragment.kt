package com.example.playlistmaker.library.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistBinding

class PlaylistFragment: Fragment() {

    companion object{

        fun newInstance() = PlaylistFragment()
    }

    private lateinit var binding: FragmentPlaylistBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }
}