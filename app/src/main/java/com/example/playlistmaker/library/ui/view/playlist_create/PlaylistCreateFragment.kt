package com.example.playlistmaker.library.ui.view.playlist_create

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.core.graphics.drawable.toBitmap
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistCreatorBinding
import com.example.playlistmaker.library.ui.viewmodel.playlist_create.PlaylistCreateViewModel
import com.example.playlistmaker.models.Playlist
import com.example.playlistmaker.utils.converters.bytesEqualTo
import com.example.playlistmaker.utils.converters.pixelsEqualTo
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

open class PlaylistCreateFragment : Fragment() {

    private var _binding: FragmentPlaylistCreatorBinding? = null
    val binding: FragmentPlaylistCreatorBinding
        get() = _binding!!

    val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        binding.ivNewImage.background = null
        Glide.with(requireContext())
            .load(uri)
            .error(requireContext().getDrawable(R.drawable.music_note))
            .into(binding.ivNewImage)
    }
    private val viewModel by viewModel<PlaylistCreateViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlaylistCreatorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBtnBack.setOnClickListener {
            if (binding.ivNewImage.drawable != null
                || !binding.etPlaylistName.text.isNullOrEmpty()
                || !binding.etPlaylistOverview.text.isNullOrEmpty()
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
                val imageName = "${getNameForImage(playlistName = binding.etPlaylistName.text.toString())}.jpg"
                addPlaylist(imageName)
                if (binding.ivNewImage.drawable != null &&
                    !binding.ivNewImage.drawable.bytesEqualTo(requireContext().getDrawable(R.drawable.music_note)) &&
                    !binding.ivNewImage.drawable.pixelsEqualTo(requireContext().getDrawable(R.drawable.music_note)))
                    viewModel.saveImageToStorage(
                        imageName,
                        binding.ivNewImage.drawable.toBitmap()
                    )
                Toast.makeText(
                    requireContext(),
                    "Плейлист ${binding.etPlaylistName.text.toString()} создан",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().popBackStack()
            }
        }

        binding.etPlaylistName.addTextChangedListener(
            onTextChanged = { text, _, _, _ ->
                binding.tvCreate.isEnabled = !text.isNullOrEmpty()
            }
        )

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
                        || !binding.etPlaylistName.text.isNullOrEmpty()
                        || !binding.etPlaylistOverview.text.isNullOrEmpty()
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
        val dialog = MaterialAlertDialogBuilder(requireContext(), R.style.DialogButtons)
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNegativeButton("Отмена") { dialog, which ->
            }
            .setPositiveButton("Да") { dialog, which ->
                findNavController().popBackStack()
            }
        dialog.show()
    }

    private fun addPlaylist(imageName: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            if (!binding.etPlaylistName.text.isNullOrEmpty()) {
                viewModel.addPlaylist(
                    Playlist(
                        0,
                        binding.etPlaylistName.text.toString(),
                        binding.etPlaylistOverview.text.toString(),
                        imageName,
                        listOf()
                    )
                )
            }
        }
    }

    fun loadImage() {
        pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
    }

    fun getNameForImage(playlistName: String) = "${playlistName}_${System.currentTimeMillis()}"

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}