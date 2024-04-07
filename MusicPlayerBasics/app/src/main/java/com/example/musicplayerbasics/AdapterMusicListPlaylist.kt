package com.example.musicplayerbasics

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerbasics.FavouritesFragment.Companion.favSong
import com.example.musicplayerbasics.databinding.PlaylistViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder

class AdapterMusicListPlaylist(private val context: Context, private var playlistList: ArrayList<Playlist>) : RecyclerView.Adapter<AdapterMusicListPlaylist.MyHolder>() {

    class MyHolder(binding: PlaylistViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.playlistIMG
        val name = binding.playlistName
        val root = binding.root
        val delete = binding.delBTN
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        if(MainActivity.themeIndex == 4){
            holder.root.strokeColor = ContextCompat.getColor(context, R.color.white)
        }
        holder.name.text = playlistList[position].name
        holder.name.isSelected = true
        holder.delete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(playlistList[position].name)
                .setMessage("Do you want to delete playlist?")
                .setPositiveButton("Yes"){ dialog, _ ->
                    PlaylistsFragment.musicPlaylist.ref.removeAt(position)
                    refreshPlaylist()
                            // Сохранение данных в SharedPreferences
        val editor = context.getSharedPreferences("FAVOURITES", AppCompatActivity.MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(favSong)
        editor.putString("FavouriteSongs", jsonString)

        val jsonStringPL = GsonBuilder().create().toJson(PlaylistsFragment.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPL)
        editor.apply()
                    dialog.dismiss()
                    if(PlaylistsFragment.musicPlaylist.ref.isNotEmpty()) PlaylistsFragment.binding.instructionPA.visibility = View.GONE
                    else PlaylistsFragment.binding.instructionPA.visibility = View.VISIBLE
                }
                .setNegativeButton("No"){dialog, _ ->
                    dialog.dismiss()
                }
            val customDialog = builder.create()
            customDialog.show()

            setDialogBtnBackground(context, customDialog)
        }
        holder.root.setOnClickListener {
            val bottomSheet = PlaylistDetailsFragment()
            val bundle = Bundle()

            bundle.putInt("index", position)
            bottomSheet .arguments = bundle
            bottomSheet .show((context as AppCompatActivity).supportFragmentManager, bottomSheet .tag)
        }
        if(PlaylistsFragment.musicPlaylist.ref[position].playlist.size > 0){
            Glide.with(context)
                .load(PlaylistsFragment.musicPlaylist.ref[position].playlist[0].artURI)
                .apply(RequestOptions().placeholder(R.drawable.icon).centerCrop())
                .into(holder.image)
        }
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }
    fun refreshPlaylist(){
        playlistList = ArrayList()
        playlistList.addAll(PlaylistsFragment.musicPlaylist.ref)
        notifyDataSetChanged()
    }
}