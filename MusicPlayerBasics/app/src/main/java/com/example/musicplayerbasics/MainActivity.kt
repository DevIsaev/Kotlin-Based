package com.example.musicplayerbasics

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerbasics.PlayerFragment.Companion.musicService
import com.example.musicplayerbasics.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import java.io.File
import kotlin.system.exitProcess


class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding:ActivityMainBinding

    var drawer: DrawerLayout? = null

    private lateinit var musicAdapter:AdapterMusicList
    companion object{
        lateinit var MusicListMA:ArrayList<Music>
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.coolPinkNav)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(requestRuntimePermission()) {
            initialization()
        }

        Navigation()

        binding.shuffleBTN.setOnClickListener {
            val bottomSheet = PlayerFragment.newInstance()
            val bundle = Bundle()
            bundle.putInt("index", 0)
            bundle.putString("class", "MainActivity")
            bottomSheet.arguments = bundle
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(!PlayerFragment.isPlaying&&PlayerFragment.musicService!=null){
            PlayerFragment.musicService!!.stopForeground(true)
            PlayerFragment.musicService!!.mediaPlayer!!.release()
            PlayerFragment.musicService=null
            exitProcess(1)
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    private fun initialization(){

        MusicListMA = getAllAudio()

        var rv=findViewById<RecyclerView>(R.id.list_music)
        rv.setHasFixedSize(true)
        rv.setItemViewCacheSize(13)
        rv.layoutManager= LinearLayoutManager(this@MainActivity)
        musicAdapter=AdapterMusicList(this@MainActivity, MusicListMA)
        rv.adapter=musicAdapter

        var totalSongs = findViewById<TextView>(R.id.totalSongs)
        totalSongs.text = "Всего песен: ${musicAdapter.itemCount}"
    }


    //получение всех аудио
    @SuppressLint("Recycle", "Range")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun getAllAudio(): ArrayList<Music> {
        val tempList = ArrayList<Music>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID,
        )

        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC", null
        )

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val titleC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)) ?: "Неизвестно"
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)) ?: "Неизвестно"
                    val albumC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)) ?: "Неизвестный"
                    val artistC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)) ?: "Неизвестно"
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val durationC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val albumIdC = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)).toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, albumIdC).toString()

                    // Добавьте проверку, чтобы загружать аудио только из папки "Music"
                    if (pathC != null && pathC.contains("/Music/")) {
                        val music = Music(id = idC, title = titleC, album = albumC, artist = artistC, path = pathC, duration = durationC, artURI = artUriC)
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

    //запрос разрешения
    private fun requestRuntimePermission() :Boolean{
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 13)
                return false
            }
        }
        //android 13 permission request
        else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_MEDIA_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO), 13)
                return false
            }
        }
        return true
    }
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 13){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted",Toast.LENGTH_SHORT).show()
                initialization()
            }
            else
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), 13)
        }
    }
    //Navigation drawer
    private fun Navigation(){
        var NavView=findViewById<NavigationView>(R.id.NavView)
        NavView.setNavigationItemSelectedListener(this)
        drawer = findViewById(R.id.Drawer)
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.IDSignOut -> {
                Toast.makeText(this, "IDSignOut", Toast.LENGTH_SHORT).show()
                Out()
            }

            R.id.IDMusicStorage -> {
                val itent=Intent(this,MainActivity::class.java)
                startActivity(itent)
            }

            R.id.IDMusicFavourite -> {
                val itent=Intent(this,FavouritesActivity::class.java)
                startActivity(itent)
            }
            R.id.IDMusicDownload -> {
                Toast.makeText(this, "IDMusicDownload", Toast.LENGTH_SHORT).show()
            }

            R.id.IDStore -> {
                Toast.makeText(this, "IDStore", Toast.LENGTH_SHORT).show()
            }

            R.id.IDPlaylists -> {
                val itent=Intent(this,PlaylistsActivity::class.java)
                startActivity(itent)
            }

            R.id.IDStatistic -> {
                Toast.makeText(this, "IDStatistic", Toast.LENGTH_SHORT).show()
            }

            R.id.IDSettings -> {
                Toast.makeText(this, "IDSettings", Toast.LENGTH_SHORT).show()
            }

            R.id.IDPlugIn -> {
                Toast.makeText(this, "IDPlugIn", Toast.LENGTH_SHORT).show()
            }

            R.id.ID_F_A_Q -> {
                Toast.makeText(this, "ID_F_A_Q", Toast.LENGTH_SHORT).show()
            }

            R.id.IDAbout -> {
                Toast.makeText(this, "IDAbout", Toast.LENGTH_SHORT).show()
            }
        }
        drawer?.closeDrawer(GravityCompat.START)
        return true
    }

    fun Out() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Выход")
        builder.setMessage("Вы действительно хотите выйти?")
        builder.setPositiveButton("Да",
            DialogInterface.OnClickListener { dialogInterface, i ->
                musicService!!.mediaPlayer!!.stop()
                finish()
            })
        builder.setNegativeButton("Нет",
            DialogInterface.OnClickListener { dialogInterface, i -> })
        builder.show()
    }
}