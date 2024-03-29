package com.example.playlistmaker.settings.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.application.PlaylistMakerApplication
import com.example.playlistmaker.databinding.FragmentSettingsBinding
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding: FragmentSettingsBinding
        get() = _binding!!

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.flBtnShareApplication.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.practicum_android_developer_link))
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(intent, null)
            startActivity(shareIntent)
        }

        binding.flBtnSupportConnect.setOnClickListener {
            val subject = getString(R.string.support_connect_subject)
            val message = getString(R.string.support_connect_message)
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.developer_mail_address))
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, message)
        }

        binding.flBtnUserAgreement.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.practicum_offer_link)))
            startActivity(intent)
        }

        binding.themeSwitch.isChecked = (requireActivity().applicationContext as PlaylistMakerApplication).darkTheme

        binding.themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            (requireActivity().applicationContext as PlaylistMakerApplication).switchDarkTheme(isChecked)

            viewModel.changeTheme(isChecked)
        }
    }
}