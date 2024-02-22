package com.example.musicplayerbasics

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerbasics.databinding.FeaturesBinding
import com.example.musicplayerbasics.databinding.MusicViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File




class AdapterMusicList(private val context: Context, private var musicList: ArrayList<Music>, private  var playlistDetails:Boolean=false, private var selectionActivity:Boolean=false) : RecyclerView.Adapter<AdapterMusicList.MyHolder>(){

    class MyHolder(binding: MusicViewBinding):RecyclerView.ViewHolder(binding.root) {
        val title=binding.songName
        val artist=binding.songArtist
        val img=binding.imgOfMusic
        val duration=binding.songDuration
        val root=binding.root
        val card=binding.cardSong
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

        val cardView = holder.card
        val themeColors = TypedValue()
        context.theme.resolveAttribute(R.attr.rcColor, themeColors, true)
        cardView.setBackgroundColor(themeColors.data)


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
                        holder.root.setBackgroundColor(themeColors.data)
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


        if(!selectionActivity){
            holder.root.setOnLongClickListener {
                val customDialog = LayoutInflater.from(context).inflate(R.layout.features, holder.root, false)
                val bindingMF = FeaturesBinding.bind(customDialog)
                val dialog = MaterialAlertDialogBuilder(context).setView(customDialog)
                    .create()
                dialog.show()
                dialog.window?.setBackgroundDrawable(ColorDrawable(0x99000000.toInt()))

                bindingMF.infoBtn.setOnClickListener {
                    dialog.dismiss()
                    Toast.makeText(context,"Info",Toast.LENGTH_LONG).show()
                }

                bindingMF.favBtn.setOnClickListener {
                    try {
                        PlayerFragment.fIndex = favouriteCheck(musicList[position].id)
                        if (PlayerFragment.isFavourite) {
                            PlayerFragment.isFavourite = false
                            FavouritesActivity.favSong.removeAt(PlayerFragment.fIndex)
                            dialog.dismiss()
                        } else {
                            PlayerFragment.isFavourite = true
                            FavouritesActivity.favSong.add(musicList[position])
                            Toast.makeText(context, "Композиция добавлена в Избранное", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()
                        }
                    } catch (ex: Exception) {
                        // Обработайте исключение, если что-то пошло не так
                        bindingMF.favBtn.text = ex.toString()
                    }
                }

                bindingMF.playlistBtn.setOnClickListener {

                }
                bindingMF.delBtn.setOnClickListener {
                    // Получите URI выбранной композиции
                    val selectedMusic = musicList[position]
                    val contentResolver = context.contentResolver
                    val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    val selection = "${MediaStore.Audio.Media._ID} = ?"
                    val selectionArgs = arrayOf(selectedMusic.id.toString())

                    // Удалим композицию из телефона через URI
                    contentResolver.delete(uri, selection, selectionArgs)

                    // Теперь удалим файл напрямую с использованием вашей функции deleteRecursive
                    val file = File(selectedMusic.path)
                    val result = file.delete()

                    // Обновим список композиций в адаптере
                    musicList.removeAt(position)
                    notifyItemRemoved(position)

                    dialog.dismiss()

                    if (result)
                        Toast.makeText(context, "Композиция удалена", Toast.LENGTH_SHORT).show()
                }
                return@setOnLongClickListener true
            }
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }
    fun deleteRecursive(fileOrDirectory: File) {
        if (fileOrDirectory.isDirectory) for (child in fileOrDirectory.listFiles()) deleteRecursive(
            child
        )
        fileOrDirectory.delete()
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

