package com.example.musicplayerbasics

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayerbasics.databinding.FragmentFavouritesBinding
import com.google.gson.GsonBuilder


class FavouritesFragment : Fragment() {

    companion object {
        var favouritesChanged: Boolean = false
        var favSong:ArrayList<Music> = ArrayList()
        lateinit var adapter: AdapterMusicListFavourite
        lateinit var binding:FragmentFavouritesBinding
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
           initialization()
        }
        catch (ex:Exception){
            Toast.makeText(requireContext(),ex.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun initialization() {
        MainActivity.search = false
        favSong = playlistCheck(favSong)

        binding.favouriteRV.setHasFixedSize(true)
        binding.favouriteRV.setItemViewCacheSize(13)
        binding.favouriteRV.layoutManager = GridLayoutManager(requireContext(), 3)
        adapter = AdapterMusicListFavourite(requireContext(), favSong)
        binding.favouriteRV.adapter = adapter

        favouritesChanged = false

        binding.refreshLayout.setOnRefreshListener {
            adapter.updateFavourites(favSong)
            favouritesChanged = false

            binding.refreshLayout.isRefreshing = false
        }

        if(favSong.isNotEmpty()) binding.instructionFV.visibility = View.GONE
        else binding.instructionFV.visibility = View.VISIBLE
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        adapter.updateFavourites(favSong)
//        // Сохранение данных в SharedPreferences
        val editor = requireContext().getSharedPreferences("FAVOURITES", AppCompatActivity.MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(favSong)
        editor.putString("FavouriteSongs", jsonString)

        val jsonStringPL = GsonBuilder().create().toJson(PlaylistsFragment.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPL)
        editor.apply()
        if(favSong.isNotEmpty()) binding.instructionFV.visibility = View.GONE
        else binding.instructionFV.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}