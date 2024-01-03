package com.example.playlistmaker.library.ui.view.playlist_edit

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.library.ui.view.playlist_create.PlaylistCreateFragment
import com.example.playlistmaker.library.ui.viewmodel.playlist_edit.PlaylistEditViewModel
import com.example.playlistmaker.models.Playlist
import com.example.playlistmaker.utils.PLAYLIST_ID
import com.example.playlistmaker.utils.converters.bytesEqualTo
import com.example.playlistmaker.utils.converters.createPlaylistFromJson
import com.example.playlistmaker.utils.converters.pixelsEqualTo
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistEditFragment: PlaylistCreateFragment() {

    private val viewModel by viewModel<PlaylistEditViewModel>()
    private lateinit var playlist: Playlist
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlist = createPlaylistFromJson(requireArguments().getString(PLAYLIST_ID))
        initView()

        binding.ivBtnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.tvCreate.setOnClickListener {
            val imageName = "${getNameForImage(playlistName = binding.etPlaylistName.text.toString())}.jpg"
            updateImage(imageName)
            updatePlaylist(imageName)
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        })
    }

    private fun initView(){
        binding.tvCreate.text = "Сохранить"
        binding.etPlaylistName.setText(playlist.name)
        binding.etPlaylistOverview.setText(playlist.overview)
        binding.ivNewImage.background = null
        Glide.with(requireContext())
            .load(viewModel.getImage(playlist.imageName))
            .placeholder(R.drawable.music_note)
            .into(binding.ivNewImage)
    }

    private fun updateImage(newImageName: String){
        viewModel.deleteOldImage(playlist.imageName)
        if (binding.ivNewImage.drawable != null &&
            !binding.ivNewImage.drawable.bytesEqualTo(requireContext().getDrawable(R.drawable.music_note)) &&
            !binding.ivNewImage.drawable.pixelsEqualTo(requireContext().getDrawable(R.drawable.music_note)))
            viewModel.saveImageToStorage(
                newImageName,
                binding.ivNewImage.drawable.toBitmap()
            )
    }

    private fun updatePlaylist(imageName: String){
        viewModel.updatePlaylist(
            Playlist(
                playlist.id,
                binding.etPlaylistName.text.toString(),
                binding.etPlaylistOverview.text.toString(),
                imageName,
                playlist.tracks
            )
        )
    }
}