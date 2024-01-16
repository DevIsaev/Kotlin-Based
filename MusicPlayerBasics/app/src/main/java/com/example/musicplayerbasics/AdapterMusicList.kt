package com.example.musicplayerbasics

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerbasics.databinding.MusicViewBinding

class AdapterMusicList(private val context: Context, private var musicList: ArrayList<Music>, private  var playlistDetails:Boolean=false) : RecyclerView.Adapter<AdapterMusicList.MyHolder>(){
    class MyHolder(binding: MusicViewBinding):RecyclerView.ViewHolder(binding.root) {

        val title=binding.songName
        val artist=binding.songArtist
        val img=binding.imgOfMusic
        val duration=binding.songDuration
        val root=binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterMusicList.MyHolder {
        return MyHolder(MusicViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }


    override fun onBindViewHolder(holder: AdapterMusicList.MyHolder, position: Int) {
        holder.title.text = musicList[position].title
        holder.artist.text = musicList[position].artist
        holder.duration.text = DurationFormat(musicList[position].duration)

        Glide.with(context)
            .load(musicList[position].artURI)
            .apply(RequestOptions().placeholder(R.drawable.icon).centerCrop())
            .into(holder.img)


        when {
            playlistDetails ->{
                holder.root.setOnClickListener {
                    openFragment("AdapterMusicListPlaylist", position)
                }
            }
            else->{
                holder.root.setOnClickListener {
                    when {
                        MainActivity.search -> openFragment("MusicAdapterSearch", position)
                        musicList[position].id == PlayerFragment.nowPlayingId -> openFragment(
                            "NowPlaying",
                            position
                        )
                        else -> openFragment("MusicAdapter", position)
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return musicList.size
    }

//поиск
    fun updateMusicList(searchList:ArrayList<Music>){
        musicList= ArrayList()
        musicList.addAll(searchList)
        notifyDataSetChanged()
    }
    private fun openFragment(reference: String,position:Int){
        val playerFragment = PlayerFragment.newInstance()

        val bundle = Bundle()
        bundle.putInt("index", position)
        bundle.putString("class", reference)
        playerFragment.arguments = bundle

        playerFragment.show((context as AppCompatActivity).supportFragmentManager, playerFragment.tag)
    }
}

