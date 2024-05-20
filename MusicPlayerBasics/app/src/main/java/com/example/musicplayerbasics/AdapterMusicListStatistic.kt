package com.example.musicplayerbasics

import android.content.Context
import android.os.Build
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerbasics.databinding.MusicViewStatisticBinding


class AdapterMusicListStatistic(private val context: Context, private var musicList: ArrayList<Music>) : RecyclerView.Adapter<AdapterMusicListStatistic.MyHolder>(){

    class MyHolder(binding: MusicViewStatisticBinding):RecyclerView.ViewHolder(binding.root) {
        val title=binding.songName
        val artist=binding.songArtist
        val img=binding.imgOfMusic
        val count=binding.countTxt
        val root=binding.root
        val card=binding.cardSong
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(MusicViewStatisticBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int {
        return musicList.size
    }


    @RequiresApi(Build.VERSION_CODES.R)
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.title.text = musicList[position].title
        holder.artist.text = musicList[position].artist
        holder.count.text = musicList[position].count.toString()

        Glide.with(context)
            .load(musicList[position].artURI)
            .apply(RequestOptions().placeholder(R.drawable.icon).centerCrop())
            .into(holder.img)

        val cardView = holder.card
        val themeColors = TypedValue()
        context.theme.resolveAttribute(R.attr.rcColor, themeColors, true)
        cardView.setBackgroundColor(themeColors.data)



        holder.root.setOnClickListener {

        }

    }
}

