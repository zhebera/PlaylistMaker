package com.example.playlistmaker.library.ui.view

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class FragmentLibraryAdapter(fragment: Fragment): FragmentStateAdapter(fragment){
    override fun getItemCount() = 2

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> TracksFragment.newInstance()
            else -> PlaylistFragment.newInstance()
        }
    }
}