package com.example.playlistmaker

import android.app.Application
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate

const val DARK_THEME_ENABLED = "dark_theme_enabled"
const val SHARED_PREFERENCES_THEME_KEY = "shared_preferences_theme_key"

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnMainActivity = findViewById<ImageView>(R.id.btn_main_activity)
        val btnShareApplication = findViewById<FrameLayout>(R.id.btn_share_application)
        val btnSupportConnect = findViewById<FrameLayout>(R.id.btn_support_connect)
        val btnUserAgreement = findViewById<FrameLayout>(R.id.btn_user_agreement)
        val themeSwitch = findViewById<Switch>(R.id.themeSwitch)

        btnMainActivity.setOnClickListener {
            finish()
        }

        btnShareApplication.setOnClickListener {
            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, getString(R.string.practicum_android_developer_link))
                type = "text/plain"
            }
            val shareIntent = Intent.createChooser(intent,null)
            startActivity(shareIntent)
        }

        btnSupportConnect.setOnClickListener {
            val subject = getString(R.string.support_connect_subject)
            val message = getString(R.string.support_connect_message)
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, getString(R.string.developer_mail_address))
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, message)
        }

        btnUserAgreement.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.practicum_offer_link)))
            startActivity(intent)
        }

        themeSwitch.isChecked = (applicationContext as DarkThemeApp).darkTheme

        themeSwitch.setOnCheckedChangeListener { switcher, isChecked ->
            (applicationContext as DarkThemeApp).switchDarkTheme(isChecked)

            val sharedPreferences = getSharedPreferences(DARK_THEME_ENABLED, MODE_PRIVATE)
            sharedPreferences.edit()
                .putBoolean(SHARED_PREFERENCES_THEME_KEY, isChecked)
                .apply()
        }
    }
}

class DarkThemeApp: Application(){

    var darkTheme: Boolean = false

    override fun onCreate() {
        super.onCreate()

        val sharedPreferences = getSharedPreferences(DARK_THEME_ENABLED, MODE_PRIVATE)
        darkTheme = sharedPreferences.getBoolean(SHARED_PREFERENCES_THEME_KEY, false)
        switchDarkTheme(darkTheme)
    }

    fun switchDarkTheme(darkThemeEnabled: Boolean){
        darkTheme = darkThemeEnabled

        AppCompatDelegate.setDefaultNightMode(
            if(darkThemeEnabled)
                AppCompatDelegate.MODE_NIGHT_YES
            else
                AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}