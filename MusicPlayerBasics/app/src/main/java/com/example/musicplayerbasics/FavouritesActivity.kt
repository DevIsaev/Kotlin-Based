package com.example.musicplayerbasics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayerbasics.databinding.ActivityFavouritesBinding

class FavouritesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouritesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFavouritesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme(R.style.coolPink)
    }
}