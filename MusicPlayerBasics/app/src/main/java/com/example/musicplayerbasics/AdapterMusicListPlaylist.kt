package com.example.musicplayerbasics

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerbasics.databinding.PlaylistViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AdapterMusicListPlaylist(private val context: Context, private var playlistList: ArrayList<Playlist>) : RecyclerView.Adapter<AdapterMusicListPlaylist.MyHolder>(){

    class MyHolder(binding: PlaylistViewBinding): RecyclerView.ViewHolder(binding.root) {
        val img=binding.playlistIMG
        val name=binding.playlistName
        var root=binding.root

        var delete=binding.delBTN
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterMusicListPlaylist.MyHolder {
        return MyHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }


    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text=playlistList[position].name
        holder.name.isSelected=true

        holder.delete.setOnClickListener {
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(playlistList[position].name)
                .setMessage("Вы действительно хотите удалить этот плейлист?")
                .setPositiveButton("Да"){dialog,_ ->
                    PlaylistsFragment.musicPlaylist.ref.removeAt(position)
                    refrershPlaylist()
                    dialog.dismiss()
                }
                .setNegativeButton("Нет"){dialog,_ -> dialog.dismiss()}
            val customDialog=builder.create()
            customDialog.show()
        }

        holder.root.setOnClickListener {
            val bottomSheet = PlaylistDetailsFragment()
            val bundle = Bundle()

            bundle.putInt("index", position)
            bottomSheet .arguments = bundle
            bottomSheet .show((context as AppCompatActivity).supportFragmentManager, bottomSheet .tag)
        }
        if(PlaylistsFragment.musicPlaylist.ref[position].playlist.size>0){
            Glide.with(context)
                .load(PlaylistsFragment.musicPlaylist.ref[position].playlist[0].artURI)
                .apply(RequestOptions().placeholder(R.drawable.icon).centerCrop())
                .into(holder.img)
        }
    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

    fun refrershPlaylist(){
        playlistList=ArrayList()
        playlistList.addAll(PlaylistsFragment.musicPlaylist.ref)
        notifyDataSetChanged()
    }

}