package com.example.musicplayerbasics

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerbasics.databinding.MusicViewBinding

class AdapterMusicList(private val context: Context,
                       private var musicList: ArrayList<Music>,
                       private  var playlistDetails:Boolean=false,
                       private var selectionActivity:Boolean=false) : RecyclerView.Adapter<AdapterMusicList.MyHolder>(){
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
            selectionActivity ->{
                holder.root.setOnClickListener {
                    if(addSong(musicList[position]))
                        holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.blue))
                    else
                        holder.root.setBackgroundColor(ContextCompat.getColor(context, R.color.white))

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

    //добавление композиции в плейлист
    private fun addSong(song: Music): Boolean{
        PlaylistsActivity.musicPlaylist.ref[PlaylistDetailsFragment.currentPlaylistPos].playlist.forEachIndexed { index, music ->
            if(song.id == music.id){
                PlaylistsActivity.musicPlaylist.ref[PlaylistDetailsFragment.currentPlaylistPos].playlist.removeAt(index)
                return false
            }
        }
        PlaylistsActivity.musicPlaylist.ref[PlaylistDetailsFragment.currentPlaylistPos].playlist.add(song)
        return true
    }
    @SuppressLint("NotifyDataSetChanged")
    fun refreshPlaylist(){
        musicList = ArrayList()
        musicList = PlaylistsActivity.musicPlaylist.ref[PlaylistDetailsFragment.currentPlaylistPos].playlist
        notifyDataSetChanged()
    }

}

