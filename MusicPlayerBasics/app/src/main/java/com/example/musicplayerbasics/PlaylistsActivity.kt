package com.example.musicplayerbasics

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayerbasics.databinding.ActivityPlaylistsBinding
import com.example.musicplayerbasics.databinding.CustomAlertdialogAddPlaylistBinding
import com.google.android.material.navigation.NavigationView
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistsActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityPlaylistsBinding
    private lateinit var adapter:AdapterMusicListPlaylist

    var drawer: DrawerLayout? = null

    companion object{
        var musicPlaylist:PlaylistMusic=PlaylistMusic()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        binding= ActivityPlaylistsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {

            Navigation()

            //перезапись
            FavouritesActivity.favSong = ArrayList()
            val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
            val jsonString = editor.getString("FavouriteSongs", null)
            val typeToken = object : TypeToken<ArrayList<Music>>() {}.type
            if (jsonString != null) {
                val data: ArrayList<Music> = GsonBuilder().create().fromJson(jsonString, typeToken)
                FavouritesActivity.favSong.addAll(data)
            }
            PlaylistsActivity.musicPlaylist = PlaylistMusic()
            //val editorPL = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
            val jsonStringPL = editor.getString("MusicPlaylist", null)
            //val typeTokenPL = object : TypeToken<PlaylistMusic>(){}.type
            if (jsonStringPL != null) {
                val dataPL: PlaylistMusic =
                    GsonBuilder().create().fromJson(jsonStringPL, PlaylistMusic::class.java)
                PlaylistsActivity.musicPlaylist = dataPL
            }


            binding.playlistsRV.setHasFixedSize(true)
            binding.playlistsRV.setItemViewCacheSize(13)
            binding.playlistsRV.layoutManager = GridLayoutManager(this@PlaylistsActivity, 2)
            adapter = AdapterMusicListPlaylist(this@PlaylistsActivity, musicPlaylist.ref)
            binding.playlistsRV.adapter = adapter

            binding.addBtn.setOnClickListener {
                customAlertDialog()
            }
        }
        catch (ex:Exception){
            Toast.makeText(this,ex.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    //Navigation drawer
    private fun Navigation(){
        val navView = findViewById<NavigationView>(R.id.NavView)
        val headerView = navView.getHeaderView(0)
        val linearHeader = headerView.findViewById<LinearLayout>(R.id.linearHeader)

        if (linearHeader == null) {
            val mainLayout = findViewById<LinearLayout>(R.id.linearHeader)
            mainLayout?.setBackgroundResource(MainActivity.currentGradient[MainActivity.themeIndex])
        } else {
            linearHeader.setBackgroundResource(MainActivity.currentGradient[MainActivity.themeIndex])
        }

        navView.setNavigationItemSelectedListener(this)
        drawer = findViewById(R.id.Drawer)
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.IDSignOut -> {
                Toast.makeText(this, "IDSignOut", Toast.LENGTH_SHORT).show()
            }

            R.id.IDMusicStorage -> {
                val itent= Intent(this,MainActivity::class.java)
                startActivity(itent)
            }

            R.id.IDMusicFavourite -> {
                val itent= Intent(this,FavouritesActivity::class.java)
                startActivity(itent)
            }
            R.id.IDMusicDownload -> {
                Toast.makeText(this, "IDMusicDownload", Toast.LENGTH_SHORT).show()
            }

            R.id.IDStore -> {
                Toast.makeText(this, "IDStore", Toast.LENGTH_SHORT).show()
            }

            R.id.IDPlaylists -> {
                return false
            }

            R.id.IDStatistic -> {
                Toast.makeText(this, "IDStatistic", Toast.LENGTH_SHORT).show()
            }

            R.id.IDSettings -> {
                val itent=Intent(this,Settings::class.java)
                startActivity(itent)
            }

            R.id.IDPlugIn -> {
                Toast.makeText(this, "IDPlugIn", Toast.LENGTH_SHORT).show()
            }

            R.id.ID_F_A_Q -> {
                Toast.makeText(this, "ID_F_A_Q", Toast.LENGTH_SHORT).show()
            }

            R.id.IDAbout -> {
                val itent=Intent(this,About::class.java)
                startActivity(itent)
            }
        }
        drawer?.closeDrawer(GravityCompat.START)
        return false
    }

    private fun  customAlertDialog(){
        val customDialog=LayoutInflater.from(this@PlaylistsActivity).inflate(R.layout.custom_alertdialog_add_playlist,binding.root,false)

        val binder=CustomAlertdialogAddPlaylistBinding.bind(customDialog)

        val builder = AlertDialog.Builder(this)
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
            Toast.makeText(this,"Такой плейлист уже существует",Toast.LENGTH_SHORT).show()
        }
        else{
            var tempPlaylist=Playlist()
            tempPlaylist.name=name
            tempPlaylist.playlist=ArrayList()
            tempPlaylist.createdBy=user

            var calendar=java.util.Calendar.getInstance().time
            var sdf=SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
            tempPlaylist.createdOn=sdf.format(calendar)

            musicPlaylist.ref.add(tempPlaylist)
            adapter.refrershPlaylist()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }

}