package com.example.playlistmaker.library.ui.view.playlist_edit

import android.os.Bundle
import android.view.View
import com.example.playlistmaker.library.ui.view.playlist_create.PlaylistCreateFragment
import com.example.playlistmaker.library.ui.viewmodel.playlist_edit.PlaylistEditViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistEditFragment: PlaylistCreateFragment() {

    private val viewModel by viewModel<PlaylistEditViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}