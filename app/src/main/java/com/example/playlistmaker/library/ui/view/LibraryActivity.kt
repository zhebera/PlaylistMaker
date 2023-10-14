package com.example.playlistmaker.library.ui.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityLibraryBinding
import com.google.android.material.tabs.TabLayoutMediator

class LibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryBinding
    private lateinit var tabLayoutMediator: TabLayoutMediator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnMainActivity.setOnClickListener {
            finish()
        }

        binding.viewPager.adapter = FragmentLibraryAdapter(supportFragmentManager, lifecycle)

        tabLayoutMediator = TabLayoutMediator(binding.tabLayout, binding.viewPager){ tab, position ->
            tab.text = when(position){
                0 -> resources.getString(R.string.selected_tracks)
                1 -> resources.getString(R.string.playlists)
                else -> ""
            }
        }

        tabLayoutMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabLayoutMediator.detach()
    }
}