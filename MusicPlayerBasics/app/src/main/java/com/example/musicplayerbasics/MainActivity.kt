package com.example.musicplayerbasics

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayerbasics.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.favouritesBTN.setOnClickListener {
            val itent=Intent(this,FavouritesActivity::class.java)
            startActivity(itent)
        }
        binding.shuffleBTN.setOnClickListener {
            val itent=Intent(this,PlayerActivity::class.java)
            startActivity(itent)
        }
        binding.playlistsBTN.setOnClickListener {
            val itent=Intent(this,PlaylistsActivity::class.java)
            startActivity(itent)
        }
    }
}