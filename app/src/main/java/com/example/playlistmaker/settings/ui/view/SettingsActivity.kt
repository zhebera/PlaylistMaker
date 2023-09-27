package com.example.playlistmaker.settings.ui.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.creator.DarkThemeApp

class SettingsActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var btnMainActivity: ImageView
    private lateinit var btnShareApplication: FrameLayout
    private lateinit var btnSupportConnect: FrameLayout
    private lateinit var btnUserAgreement: FrameLayout
    private lateinit var themeSwitch: Switch
    private lateinit var viewModel: SettingsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeView()

        viewModel = ViewModelProvider(this, SettingsViewModel.getViewModelFactory())[SettingsViewModel::class.java]

        btnMainActivity.setOnClickListener {
            finish()
        }

        btnShareApplication.setOnClickListener{
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.practicum_android_developer_link))
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(intent,null)
            startActivity(shareIntent)
        }

        btnSupportConnect.setOnClickListener{
            val subject = getString(R.string.support_connect_subject)
            val message = getString(R.string.support_connect_message)
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.developer_mail_address))
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, message)
        }

        btnUserAgreement.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.practicum_offer_link)))
            startActivity(intent)
        }

        themeSwitch.isChecked = (applicationContext as DarkThemeApp).darkTheme

        themeSwitch.setOnCheckedChangeListener { switcher, isChecked ->
            (applicationContext as DarkThemeApp).switchDarkTheme(isChecked)

            viewModel.changeTheme(isChecked)
        }
    }

    private fun initializeView(){
        btnMainActivity = binding.btnMainActivity
        btnShareApplication = binding.btnShareApplication
        btnSupportConnect = binding.btnSupportConnect
        btnUserAgreement = binding.btnUserAgreement
        themeSwitch = binding.themeSwitch
    }
}