package com.example.playlistmaker

import android.app.Application
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val btnMainActivity = findViewById<ImageView>(R.id.btn_main_activity)
        val btnShareApplication = findViewById<FrameLayout>(R.id.btn_share_application)
        val btnSupportConnect = findViewById<FrameLayout>(R.id.btn_support_connect)
        val btnUserAgreement = findViewById<FrameLayout>(R.id.btn_user_agreement)

        btnMainActivity.setOnClickListener {
            finish()
        }

        btnShareApplication.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND, Uri.parse(getString(R.string.practicum_android_developer_link)))
            startActivity(intent)
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
    }
}