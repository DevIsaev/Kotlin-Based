package com.example.musicplayerbasics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerbasics.databinding.ActivitySelectionToPlaylistBinding

class SelectionToPlaylistActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectionToPlaylistBinding
    private lateinit var adapter:AdapterMusicList

    lateinit var MusicListSearchS:ArrayList<Music>
    var searchS:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySelectionToPlaylistBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_selection_to_playlist)
        searchS =false
        setTheme(R.style.coolPink)
        var rv=findViewById<RecyclerView>(R.id.selectionRV)
        rv.setHasFixedSize(true)
        rv.setItemViewCacheSize(13)
        rv.layoutManager= LinearLayoutManager(this@SelectionToPlaylistActivity)
        adapter=AdapterMusicList(this@SelectionToPlaylistActivity, MainActivity.MusicListMA, selectionActivity = true)
        rv.adapter=adapter

        binding.searchViewSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean = true
            override fun onQueryTextChange(newText: String?): Boolean {
                MainActivity.MusicListSearch = ArrayList()
                if(newText != null){
                    val userInput = newText.lowercase()
                    for (song in MainActivity.MusicListMA)
                        if(song.title.lowercase().contains(userInput))
                            MainActivity.MusicListSearch.add(song)
                    MainActivity.search = true
                    adapter.updateMusicList(searchList = MainActivity.MusicListSearch)
                }
                return true
            }

        })
    }
}