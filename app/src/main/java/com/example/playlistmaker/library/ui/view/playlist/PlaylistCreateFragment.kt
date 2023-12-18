package com.example.playlistmaker.library.ui.view.playlist

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.core.graphics.drawable.toBitmapOrNull
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistCreatorBinding
import com.example.playlistmaker.models.Playlist
import com.example.playlistmaker.library.ui.viewmodel.playlist.PlaylistCreateViewModel
import com.example.playlistmaker.utils.PLAYLIST_STORAGE_NAME
import com.example.playlistmaker.utils.converters.getNameForImage
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class PlaylistCreateFragment : Fragment() {

    private var _binding: FragmentPlaylistCreatorBinding? = null
    private val binding: FragmentPlaylistCreatorBinding
        get() = _binding!!

    private var textWatcherName: TextWatcher? = null
    private var uriImage: Uri? = null
    private val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        uriImage = uri
        if (uri != null) {
            Glide.with(requireContext())
                .load(uri)
                .into(binding.ivNewImage)
        } else {
        }
    }
    private val viewModel by viewModel<PlaylistCreateViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentPlaylistCreatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBtnBack.setOnClickListener {
            if (binding.ivNewImage.drawable != null
                && !binding.etPlaylistName.text.isNullOrEmpty()
                && !binding.etPlaylistOverview.text.isNullOrEmpty()
            ) {
                showDialog()
            } else {
                findNavController().popBackStack()
            }
        }

        binding.ivNewImage.setOnClickListener {
            loadImage()
        }

        binding.tvCreate.setOnClickListener {
            if (it.isEnabled) {
                saveImageToStorage()
                addPlaylist()
                findNavController().popBackStack()
            } else {

            }
        }

        textWatcherName = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tvCreate.isEnabled =
                    s?.isNotEmpty() == true
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        textWatcherName.let {
            binding.etPlaylistName.addTextChangedListener(it)
        }

        binding.etPlaylistName.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                closeKeybord()
                true
            } else {
                false
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isEnabled) {
                    if (binding.ivNewImage.drawable != null
                        && !binding.etPlaylistName.text.isNullOrEmpty()
                        && !binding.etPlaylistOverview.text.isNullOrEmpty()
                    ) {
                        showDialog()
                    } else {
                        findNavController().popBackStack()
                    }
                }
            }

        })
    }

    private fun closeKeybord() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(binding.etPlaylistName.windowToken, 0)
    }

    private fun showDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNegativeButton("Отмена") { dialog, which ->
            }
            .setPositiveButton("Да") { dialog, which ->
                findNavController().popBackStack()
            }
        dialog.show()
    }

    private fun addPlaylist() {
        viewLifecycleOwner.lifecycleScope.launch {
            if (!binding.etPlaylistName.text.isNullOrEmpty()) {
                viewModel.addPlaylist(
                    Playlist(
                        binding.etPlaylistName.text.toString(),
                        binding.etPlaylistOverview.text.toString(),
                        getNameForImage(binding.etPlaylistName.text.toString()),
                        null
                    )
                )
            }
        }
    }

    private fun saveImageToStorage() {
        viewLifecycleOwner.lifecycleScope.launch {
            val filePath =
                File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), PLAYLIST_STORAGE_NAME)

            if (!filePath.exists()) {
                filePath.mkdirs()
            }

            val file = File(filePath, "${getNameForImage(playlistName = binding.etPlaylistName.text.toString())}.jpg")
            val outputStream = FileOutputStream(file)

            if (uriImage != null) {
                val inputStream = requireContext().contentResolver.openInputStream(uriImage!!)
                BitmapFactory
                    .decodeStream(inputStream)
                    .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

            }else{
                val inputStream = requireContext().getDrawable(R.drawable.music_note)?.toBitmapOrNull()
                inputStream?.compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
            }
        }
    }

    private fun loadImage() {
        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}