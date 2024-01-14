package com.example.musicplayerbasics

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerbasics.databinding.PlaylistViewBinding

class AdapterMusicListPlaylist(private val context: Context, private var playlistList: ArrayList<Playlist>) : RecyclerView.Adapter<AdapterMusicListPlaylist.MyHolder>(){

    class MyHolder(binding: PlaylistViewBinding): RecyclerView.ViewHolder(binding.root) {
        val img=binding.playlistIMG
        val name=binding.playlistName
        var root=binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterMusicListPlaylist.MyHolder {
        return MyHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }


    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text=playlistList[position].name
        holder.name.isSelected=true
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

    fun refrershPlaylist(){
        playlistList=ArrayList()
        playlistList.addAll(PlaylistsActivity.musicPlaylist.ref)
        notifyDataSetChanged()
    }

}