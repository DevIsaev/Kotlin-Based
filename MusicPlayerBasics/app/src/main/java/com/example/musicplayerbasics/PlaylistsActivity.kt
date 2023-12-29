package com.example.musicplayerbasics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayerbasics.databinding.ActivityPlaylistsBinding

class PlaylistsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlaylistsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityPlaylistsBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}