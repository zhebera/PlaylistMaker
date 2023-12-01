package com.example.playlistmaker.library.ui.view

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.playlistmaker.library.ui.view.playlist.PlaylistFragment
import com.example.playlistmaker.library.ui.view.tracks.LibraryTracksFragment

class FragmentLibraryAdapter(fragment: Fragment): FragmentStateAdapter(fragment){
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> LibraryTracksFragment.newInstance()
            else -> PlaylistFragment.newInstance()
        }
    }
}