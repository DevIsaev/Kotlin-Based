package com.example.musicplayerbasics

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerbasics.databinding.FavourViewBinding

class AdapterMusicListFavourite(private val context: Context, private var musicList: ArrayList<Music>) : RecyclerView.Adapter<AdapterMusicListFavourite.MyHolder>(){

    class MyHolder(binding: FavourViewBinding): RecyclerView.ViewHolder(binding.root) {
        val img=binding.songFart
        val name=binding.nameSong
        var root=binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterMusicListFavourite.MyHolder {
        return MyHolder(FavourViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }


    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.name.text=musicList[position].title
        Glide.with(context)
            .load(musicList[position].artURI)
            .apply(RequestOptions().placeholder(R.drawable.icon).centerCrop())
            .into(holder.img)
        holder.root.setOnClickListener {
            val playerFragment = PlayerFragment.newInstance()
            val bundle = Bundle()
            bundle.putInt("index", position)
            bundle.putString("class", "Favourite")
            playerFragment.arguments = bundle

            playerFragment.show((context as AppCompatActivity).supportFragmentManager, playerFragment.tag)
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }

}