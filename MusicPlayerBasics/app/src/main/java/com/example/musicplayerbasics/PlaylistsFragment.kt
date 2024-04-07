package com.example.musicplayerbasics

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayerbasics.databinding.CustomAlertdialogAddPlaylistBinding
import com.example.musicplayerbasics.databinding.FragmentPlaylistsBinding
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Locale


class PlaylistsFragment : Fragment() {
    private lateinit var adapter:AdapterMusicListPlaylist
    companion object {
        lateinit var binding:FragmentPlaylistsBinding
        var musicPlaylist:PlaylistMusic=PlaylistMusic()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
          intialization()
        }
        catch (ex:Exception){
            Toast.makeText(requireContext(),ex.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun intialization() {
        //перезапись
        FavouritesFragment.favSong = ArrayList()
        val editor = requireContext().getSharedPreferences("FAVOURITES", AppCompatActivity.MODE_PRIVATE)
        val jsonString = editor.getString("FavouriteSongs", null)
        val typeToken = object : TypeToken<ArrayList<Music>>() {}.type
        if (jsonString != null) {
            val data: ArrayList<Music> = GsonBuilder().create().fromJson(jsonString, typeToken)
            FavouritesFragment.favSong.addAll(data)
        }
        musicPlaylist = PlaylistMusic()
        //val editorPL = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
        val jsonStringPL = editor.getString("MusicPlaylist", null)
        //val typeTokenPL = object : TypeToken<PlaylistMusic>(){}.type
        if (jsonStringPL != null) {
            val dataPL: PlaylistMusic =
                GsonBuilder().create().fromJson(jsonStringPL, PlaylistMusic::class.java)
            musicPlaylist = dataPL
        }

        binding.playlistsRV.setHasFixedSize(true)
        binding.playlistsRV.setItemViewCacheSize(13)
        binding.playlistsRV.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = AdapterMusicListPlaylist(requireContext(), musicPlaylist.ref)
        binding.playlistsRV.adapter = adapter

        binding.addBtn.setOnClickListener {
            customAlertDialog()
        }
        if(musicPlaylist.ref.isNotEmpty()) binding.instructionPA.visibility = View.GONE
        else binding.instructionPA.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        //intialization()
        val editor = requireContext().getSharedPreferences("FAVOURITES", AppCompatActivity.MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouritesFragment.favSong)
        editor.putString("FavouriteSongs", jsonString)

        val jsonStringPL = GsonBuilder().create().toJson(PlaylistsFragment.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPL)
        editor.apply()
        adapter.notifyDataSetChanged()

        if(musicPlaylist.ref.isNotEmpty()) binding.instructionPA.visibility = View.GONE
        else binding.instructionPA.visibility = View.VISIBLE
    }

    private fun  customAlertDialog(){
        val customDialog=LayoutInflater.from(requireContext()).inflate(R.layout.custom_alertdialog_add_playlist,binding.root,false)

        val binder= CustomAlertdialogAddPlaylistBinding.bind(customDialog)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(customDialog)
            .setTitle("Создать плейлист")
            .setPositiveButton("Создать"){dialog,_ ->
                val plName=binder.playlistNAME.text
                val plUName=binder.playlistUNAME.text
                if(plName!=null&&plUName!=null){
                    if(plName.isNotEmpty()&&plUName.isNotEmpty()){
                        addPlayList(plName.toString(),plUName.toString())
                    }
                }
                dialog.dismiss()
            }.show()

    }
    private fun addPlayList(name:String,user:String){
        var exist=false
        for(i in musicPlaylist.ref){
            if(name.equals(i.name)&&user.equals(i.createdBy)){
                exist=true
                break
            }
        }
        if(exist){
            Toast.makeText(requireContext(),"Такой плейлист уже существует",Toast.LENGTH_SHORT).show()
        }
        else{
            var tempPlaylist=Playlist()
            tempPlaylist.name=name
            tempPlaylist.playlist=ArrayList()
            tempPlaylist.createdBy=user

            var calendar=java.util.Calendar.getInstance().time
            var sdf= SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempPlaylist.createdOn=sdf.format(calendar)

            musicPlaylist.ref.add(tempPlaylist)
            adapter.refreshPlaylist()

        }
        if(musicPlaylist.ref.isNotEmpty()) binding.instructionPA.visibility = View.GONE
        else binding.instructionPA.visibility = View.VISIBLE
    }
}