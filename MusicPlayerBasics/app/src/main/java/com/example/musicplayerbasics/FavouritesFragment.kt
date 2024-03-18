package com.example.musicplayerbasics

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
    private lateinit var binding:FragmentFavouritesBinding
    private lateinit var adapter: AdapterMusicListFavourite
    companion object {
        var favouritesChanged: Boolean = false
        var favSong:ArrayList<Music> = ArrayList()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
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
        }
        catch (ex:Exception){
            Toast.makeText(requireContext(),ex.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        // Сохранение данных в SharedPreferences
        val editor = requireContext().getSharedPreferences("FAVOURITES", AppCompatActivity.MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(favSong)
        editor.putString("FavouriteSongs", jsonString)

        val jsonStringPL = GsonBuilder().create().toJson(PlaylistsFragment.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPL)
        editor.apply()

        // Если произошли изменения в избранном, обновите список
        if (favouritesChanged) {
            adapter.updateFavourites(favSong)
            favouritesChanged = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}