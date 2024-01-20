package com.example.musicplayerbasics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayerbasics.databinding.ActivitySelectionToPlaylistBinding

class SelectionToPlaylistActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectionToPlaylistBinding
    private lateinit var adapter:AdapterMusicList



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectionToPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme(MainActivity.currentTheme[MainActivity.themeIndex])

        binding.selectionRV.setHasFixedSize(true)
        binding.selectionRV.setItemViewCacheSize(30)
        binding.selectionRV.layoutManager = LinearLayoutManager(this)

        adapter = AdapterMusicList(this@SelectionToPlaylistActivity, MainActivity.MusicListMA, selectionActivity = true)
        binding.selectionRV.adapter = adapter

        binding.searchViewSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchViewSA.clearFocus()
                binding.searchViewSA.setQuery("", false)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                MainActivity.MusicListSearch = ArrayList()
                if (newText != null) {
                    val input = newText.lowercase()
                    for (song in MainActivity.MusicListMA) {
                        if (song.title.lowercase().contains(input) || song.artist.lowercase().contains(input) || song.album.lowercase().contains(input)) {
                            MainActivity.MusicListSearch.add(song)
                        }
                    }
                    MainActivity.search = true
                    adapter.updateMusicList(MainActivity.MusicListSearch)
                }
                return true
            }
        })
    }
}