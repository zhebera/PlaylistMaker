package com.example.playlistmaker.library.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentMediatekaTracksBinding

class TracksFragment: Fragment() {

    private lateinit var binding: FragmentMediatekaTracksBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediatekaTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    companion object{
        fun newInstance() = TracksFragment()
    }
}