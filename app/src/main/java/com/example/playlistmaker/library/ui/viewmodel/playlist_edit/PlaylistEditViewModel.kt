package com.example.playlistmaker.library.ui.viewmodel.playlist_edit

import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.db.LibraryInteractor
import com.example.playlistmaker.library.ui.viewmodel.playlist_create.PlaylistCreateViewModel
import com.example.playlistmaker.models.Playlist
import kotlinx.coroutines.launch
import java.io.File

class PlaylistEditViewModel(context: Context,
                            private val libraryInteractor: LibraryInteractor
): PlaylistCreateViewModel(context, libraryInteractor) {

    fun getImage(name: String?) = File(filePath, name).toUri()

    fun updatePlaylist(playlist: Playlist){
        viewModelScope.launch {
            libraryInteractor.updatePlaylist(playlist)
        }
    }

    fun deleteOldImage(oldImageName: String){
        val file = File(filePath, oldImageName)
        file.delete()
    }
}