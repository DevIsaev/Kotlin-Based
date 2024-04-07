package com.example.musicplayerbasics

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableStringBuilder
import android.text.format.DateUtils
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.musicplayerbasics.databinding.DetailsViewBinding
import com.example.musicplayerbasics.databinding.FeaturesBinding
import com.example.musicplayerbasics.databinding.MusicViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.gson.GsonBuilder
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


    @RequiresApi(Build.VERSION_CODES.R)
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
            playlistDetails -> {
                holder.root.setOnClickListener {
                    openFragment("AdapterMusicListPlaylist", position)
                }
            }

            selectionActivity -> {
                holder.root.setOnClickListener {
                    if (addSong(musicList[position]))
                        holder.root.setBackgroundColor(
                            ContextCompat.getColor(
                                context,
                                R.color.blue
                            )
                        )
                    else
                        holder.root.setBackgroundColor(themeColors.data)
                }
            }

            else -> {
                holder.root.setOnClickListener {
                    when {
                        MainActivity.search -> openFragment("MusicAdapterSearch", position)
                        musicList[position].id == PlayerFragment.nowPlayingId -> openFragment("NowPlaying", position)
                        //musicList[position].id == PlayerFragment.nowPlayingId -> openFragment("MusicAdapter", position)
                        else -> openFragment("MusicAdapter", position)
                    }
                }
            }
        }


        if (!selectionActivity) {
            holder.root.setOnLongClickListener {
                when{
                    playlistDetails->
                    {
                        val builder = android.app.AlertDialog.Builder(context)
                        builder.setTitle("Удалить из плейлиста?")
                            .setPositiveButton("Да"){_, _ ->
                                val position = holder.adapterPosition
                                if (position != RecyclerView.NO_POSITION) {
                                    val songToRemove = musicList[position]
                                    val removed = removeSong(songToRemove)
                                    if (removed) {
                                        notifyItemRemoved(position)
                                        notifyDataSetChanged()
                                        Toast.makeText(context, "Композиция удалена из плейлиста", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Не удалось удалить композицию из плейлиста", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            .setNegativeButton("Нет"){dialog,_ -> dialog.dismiss()}
                        val customDialog=builder.create()
                        customDialog.show()
                    }
                    else-> {
                        val customDialog =
                            LayoutInflater.from(context)
                                .inflate(R.layout.features, holder.root, false)
                        val bindingMF = FeaturesBinding.bind(customDialog)
                        val dialog = MaterialAlertDialogBuilder(context).setView(customDialog)
                            .create()
                        dialog.show()
                        dialog.window?.setBackgroundDrawable(ColorDrawable(0x99000000.toInt()))

                        bindingMF.infoBtn.setOnClickListener {
                            dialog.dismiss()
                            val detailsDialog = LayoutInflater.from(context)
                                .inflate(R.layout.details_view, bindingMF.root, false)
                            val binder = DetailsViewBinding.bind(detailsDialog)
                            binder.detailsTV.setTextColor(Color.WHITE)
                            binder.root.setBackgroundColor(Color.TRANSPARENT)
                            val dDialog = MaterialAlertDialogBuilder(context)
//                        .setBackground(ColorDrawable(0x99000000.toInt()))
                                .setView(detailsDialog)
                                .setPositiveButton("OK") { self, _ -> self.dismiss() }
                                .setCancelable(false)
                                .create()
                            dDialog.show()
                            dDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                            setDialogBtnBackground(context, dDialog)
                            dDialog.window?.setBackgroundDrawable(ColorDrawable(0x99000000.toInt()))
                            val str =
                                SpannableStringBuilder().bold { append("Информация\n\nНазвание: ") }
                                    .append(musicList[position].title)
                                    .bold { append("\n\nПродолжительность: ") }
                                    .append(DateUtils.formatElapsedTime(musicList[position].duration / 1000))
                                    .bold { append("\n\nРасположение: ") }
                                    .append(musicList[position].path)
                            binder.detailsTV.text = str
                        }

                        bindingMF.favBtn.setOnClickListener {
                            try {
                                PlayerFragment.fIndex = favouriteCheck(musicList[position].id)
                                if (PlayerFragment.isFavourite) {
                                    PlayerFragment.isFavourite = false
                                    FavouritesFragment.favSong.removeAt(PlayerFragment.fIndex)
                                    dialog.dismiss()
                                } else {
                                    PlayerFragment.isFavourite = true
                                    FavouritesFragment.favSong.add(musicList[position])
                                    Toast.makeText(
                                        context,
                                        "Композиция добавлена в Избранное",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    // Сохранение данных в SharedPreferences
                                    val editor = context.getSharedPreferences("FAVOURITES", AppCompatActivity.MODE_PRIVATE).edit()
                                    val jsonString = GsonBuilder().create().toJson(FavouritesFragment.favSong)
                                    editor.putString("FavouriteSongs", jsonString)

                                    val jsonStringPL = GsonBuilder().create().toJson(PlaylistsFragment.musicPlaylist)
                                    editor.putString("MusicPlaylist", jsonStringPL)
                                    editor.apply()
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
                            dialog.dismiss()
                            requestDelete(position=position)
                        }
                    }
                }
                return@setOnLongClickListener true
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestDelete(position:Int) {
        val uriList: List<Uri> = listOf(Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, musicList[position].id))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val pi = MediaStore.createDeleteRequest(context.contentResolver, uriList)
            (context as Activity).startIntentSenderForResult(pi.intentSender, 123, null, 0, 0, 0, null)
        } else {
            // For devices less than Android 11
            val file = File(musicList[position].path)
            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle("Delete Audio?")
                .setMessage(musicList[position].title)
                .setPositiveButton("Yes") { self, _ ->
                    if (file.exists() && file.delete()) {
                        MediaScannerConnection.scanFile(context, arrayOf(file.path), null, null)
                        Toast.makeText(context, "Композиция была удалена", Toast.LENGTH_SHORT).show()
//                        notifyItemRemoved(position)
//                        musicList.removeAt(position)
//                        notifyDataSetChanged()
//                        MainActivity.musicAdapter.updateMusicList(musicList)
//                        // Получаем фрагмент по тегу (если он добавлен в активность)
                        val fragmentManager =
                            (context as AppCompatActivity).supportFragmentManager
                        val fragment =
                            fragmentManager.findFragmentByTag("AllMusicFragment") as? AllMusicFragment
                        fragment?.onResume()
                        when{
                            MainActivity.search-> {
                                MainActivity.MusicListMA = AllMusicFragment().getAllAudio()
                                MainActivity.MusicListMA.removeAt(position)
                                MainActivity.musicAdapter.updateMusicList(MainActivity.MusicListSearch)
                                //notifyDataSetChanged()
                                AllMusicFragment().onResume()
                            }
                            else->{
                                MainActivity.MusicListMA = AllMusicFragment().getAllAudio()
                                MainActivity.MusicListMA.removeAt(position)
                                MainActivity.musicAdapter.updateMusicList(MainActivity.MusicListMA)
                                //notifyDataSetChanged()
                                AllMusicFragment().onResume()
                            }
                        }

                    }
                    self.dismiss()
                }
                .setNegativeButton("No") { self, _ -> self.dismiss() }
            val delDialog = builder.create()
            delDialog.show()
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

//        val diffResult = DiffUtil.calculateDiff(MyDiffUtil(musicList, searchList))
//        musicList= ArrayList()
//        musicList.addAll(searchList)
//        diffResult.dispatchUpdatesTo(this)
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
        PlaylistsFragment.musicPlaylist.ref[PlaylistDetailsFragment.currentPlaylistPos].playlist.forEachIndexed { index, music ->
            if(song.id == music.id){
                PlaylistsFragment.musicPlaylist.ref[PlaylistDetailsFragment.currentPlaylistPos].playlist.removeAt(index)
                return false
            }
        }
        PlaylistsFragment.musicPlaylist.ref[PlaylistDetailsFragment.currentPlaylistPos].playlist.add(song)
        return true
    }
    @SuppressLint("NotifyDataSetChanged")
    fun refreshPlaylist(){
        musicList = ArrayList()
        musicList = PlaylistsFragment.musicPlaylist.ref[PlaylistDetailsFragment.currentPlaylistPos].playlist
        notifyItemRangeInserted(0, musicList.size)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeSong(song: Music): Boolean {
        val playlist = PlaylistsFragment.musicPlaylist.ref[PlaylistDetailsFragment.currentPlaylistPos].playlist
        val index = playlist.indexOfFirst { it.id == song.id }
        return if (index != -1) {
            playlist.removeAt(index)
            true
        } else {
            false
        }
    }
}

