package com.example.musicplayerbasics

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerbasics.databinding.ActivitySelectionToPlaylistBinding

class SelectionToPlaylistActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySelectionToPlaylistBinding
    private lateinit var adapter:AdapterMusicList



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySelectionToPlaylistBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_selection_to_playlist)
        setTheme(R.style.coolPink)

        var rv=findViewById<RecyclerView>(R.id.selectionRV)
        rv.setHasFixedSize(true)
        rv.setItemViewCacheSize(30)
        rv.layoutManager= LinearLayoutManager(this@SelectionToPlaylistActivity)
        adapter=AdapterMusicList(this@SelectionToPlaylistActivity, MainActivity.MusicListMA, selectionActivity = true)
        rv.adapter=adapter

        binding.searchViewSA.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean{
                binding.searchViewSA.clearFocus()
                binding.searchViewSA.setQuery("",false)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
//                MainActivity.MusicListSearch = ArrayList()
//                if (newText!=null){
//                    val input=newText.lowercase()
//                    for(song in MainActivity.MusicListMA) {
//                        if (song.title.lowercase().contains(input)||song.artist.lowercase().contains(input)||song.album.lowercase().contains(input)) {
//                            MainActivity.MusicListSearch.add(song)
//                        }
//                        MainActivity.search =true
//                        adapter.updateMusicList(MainActivity.MusicListSearch)
//                    }
//                }
                Toast.makeText(this@SelectionToPlaylistActivity,newText,Toast.LENGTH_SHORT).show()
                return true
            }

        })
    }
}