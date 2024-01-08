package com.example.playlistmaker.library.ui.viewmodel.playlist_create

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.db.LibraryInteractor
import com.example.playlistmaker.models.Playlist
import com.example.playlistmaker.utils.PLAYLIST_STORAGE_NAME
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

open class PlaylistCreateViewModel(
    context: Context,
    private val libraryInteractor: LibraryInteractor
) : ViewModel() {

    val filePath by lazy {
        File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), PLAYLIST_STORAGE_NAME)
    }

    suspend fun addPlaylist(playlist: Playlist) {
        libraryInteractor.addPlaylist(playlist)
    }

    fun saveImageToStorage(playlistImgName: String, image: Bitmap) {
        viewModelScope.launch {

            if (!filePath.exists()) {
                filePath.mkdirs()
            }

            val file = File(filePath, playlistImgName)
            val outputStream = FileOutputStream(file)

            image.compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        }
    }
}