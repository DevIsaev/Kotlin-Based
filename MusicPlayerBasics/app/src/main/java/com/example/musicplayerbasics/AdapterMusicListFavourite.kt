package com.example.musicplayerbasics

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerbasics.databinding.FavourViewBinding
import com.google.gson.GsonBuilder

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
            when {
                MainActivity.search -> openFragment("MusicAdapterSearch", position)
                musicList[position].id == PlayerFragment.nowPlayingId -> openFragment("NowPlaying", position)
                else -> openFragment("Favourite", position)
            }
        }
        holder.root.setOnLongClickListener {
            val builder = android.app.AlertDialog.Builder(context)
            builder.setTitle("Удалить из избранных?")
                .setPositiveButton("Да"){_, _ ->
                    PlayerFragment.fIndex = favouriteCheck(musicList[position].id)
                    if (PlayerFragment.isFavourite) {
                        PlayerFragment.isFavourite = false
                        FavouritesFragment.favSong.removeAt(PlayerFragment.fIndex)
                        Toast.makeText(context, "Композиция удалена из избранных", Toast.LENGTH_SHORT).show()
                        FavouritesFragment.adapter.updateFavourites(FavouritesFragment.favSong)
                        // Сохранение данных в SharedPreferences
                        val editor = context.getSharedPreferences("FAVOURITES", AppCompatActivity.MODE_PRIVATE).edit()
                        val jsonString = GsonBuilder().create().toJson(FavouritesFragment.favSong)
                        editor.putString("FavouriteSongs", jsonString)

                        val jsonStringPL = GsonBuilder().create().toJson(PlaylistsFragment.musicPlaylist)
                        editor.putString("MusicPlaylist", jsonStringPL)
                        editor.apply()
                        if(FavouritesFragment.favSong.isNotEmpty()) FavouritesFragment.binding.instructionFV.visibility = View.GONE
                        else FavouritesFragment.binding.instructionFV.visibility = View.VISIBLE
                    }
                }
                .setNegativeButton("Нет"){dialog,_ -> dialog.dismiss()}
            val customDialog=builder.create()
            customDialog.show()
            return@setOnLongClickListener true
        }
    }

    private fun openFragment(reference: String,position:Int){
        val playerFragment = PlayerFragment.newInstance(context)

        val bundle = Bundle()
        bundle.putInt("index", position)
        bundle.putString("class", reference)
        playerFragment.arguments = bundle
        playerFragment.show((context as AppCompatActivity).supportFragmentManager, playerFragment.tag)
    }
    override fun getItemCount(): Int {
        return musicList.size
    }
    fun updateFavourites(newList: ArrayList<Music>){
        musicList = ArrayList()
        musicList.addAll(newList)
        notifyDataSetChanged()

//        val diffResult = DiffUtil.calculateDiff(MyDiffUtil(musicList, newList))
//        musicList = ArrayList()
//        musicList.addAll(newList)
//        diffResult.dispatchUpdatesTo(this)
    }
}