package com.example.playlistmaker.library.ui.viewmodel.playlist_edit

import android.content.Context
import com.example.playlistmaker.library.domain.db.LibraryInteractor
import com.example.playlistmaker.library.ui.viewmodel.playlist_create.PlaylistCreateViewModel

class PlaylistEditViewModel(context: Context,
                            private val libraryInteractor: LibraryInteractor
): PlaylistCreateViewModel(context, libraryInteractor) {

}