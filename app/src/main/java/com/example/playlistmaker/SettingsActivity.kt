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
            val intent = Intent(Intent.ACTION_SEND, Uri.parse("https://practicum.yandex.ru/android-developer/"))
            startActivity(intent)
        }

        btnSupportConnect.setOnClickListener {
            val subject = "Сообщение разработчикам и разработчицам приложения Playlist Maker"
            val message = "Спасибо разработчикам и разработчицам за крутое приложение!"
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:")
            intent.putExtra(Intent.EXTRA_EMAIL, "zhebera@yandex.by")
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, message)
        }

        btnUserAgreement.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://yandex.ru/legal/practicum_offer/"))
            startActivity(intent)
        }
    }
}