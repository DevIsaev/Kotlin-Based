package com.example.musicplayerbasics

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayerbasics.databinding.FragmentAllMusicBinding
import java.io.File


class AllMusicFragment : Fragment() {
    private lateinit var binding: FragmentAllMusicBinding

    companion object{
        fun newInstance(): AllMusicFragment {
            return AllMusicFragment()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAllMusicBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialization()
        binding.refreshLayout.setOnRefreshListener {
            MainActivity.MusicListMA = getAllAudio()
            MainActivity.musicAdapter.updateMusicList(MainActivity.MusicListMA)
            binding.totalSongs.text = "Всего песен: ${MainActivity.musicAdapter.itemCount}"
            binding.refreshLayout.isRefreshing = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    private fun initialization() {
        //поиск
        MainActivity.search = false
        var sortEditor = requireContext().getSharedPreferences("SORTING", AppCompatActivity.MODE_PRIVATE)
        MainActivity.sort = sortEditor.getInt("sortOrder", 0)
        MainActivity.MusicListMA = getAllAudio()

        binding.listMusic.setHasFixedSize(true)
        binding.listMusic.setItemViewCacheSize(100)
        binding.listMusic.layoutManager = LinearLayoutManager(requireContext())
        MainActivity.musicAdapter = AdapterMusicList(requireContext(), MainActivity.MusicListMA)
        binding.listMusic.adapter = MainActivity.musicAdapter

        binding.totalSongs.text = "Всего песен: ${MainActivity.musicAdapter.itemCount}"

    }
    //получение всех аудио
    @SuppressLint("Recycle", "Range")
    @RequiresApi(Build.VERSION_CODES.R)
    fun getAllAudio(): ArrayList<Music> {
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = requireContext().contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            MainActivity.sortingList[MainActivity.sort],
            null
        )

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val titleC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                            ?: "Неизвестно"
                    val idC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                            ?: "Неизвестно"
                    val albumC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                            ?: "Неизвестный"
                    val artistC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                            ?: "Неизвестно"
                    val pathC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                            .toString()

                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()


                    if (pathC != null && pathC.contains("/Music/")) {
                        val music = Music(
                            id = idC,
                            title = titleC,
                            album = albumC,
                            artist = artistC,
                            path = pathC,
                            duration = durationC,
                            artURI = artUriC
                        )
                        val file = File(music.path)
                        if (file.exists())
                            tempList.add(music)
                    }
                } while (cursor.moveToNext())
            }
            cursor.close()
        }
        return tempList
    }
}