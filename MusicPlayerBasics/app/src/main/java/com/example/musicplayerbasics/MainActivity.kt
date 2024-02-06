package com.example.musicplayerbasics

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerbasics.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.File


class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding:ActivityMainBinding

    var drawer: DrawerLayout? = null
    lateinit var toggle: ActionBarDrawerToggle

    private lateinit var musicAdapter:AdapterMusicList
    companion object{
        lateinit var MusicListMA:ArrayList<Music>

        lateinit var MusicListSearch:ArrayList<Music>
        var search:Boolean=false

        var themeIndex: Int = 0
        val currentTheme = arrayOf(R.style.coolPink, R.style.coolBlue, R.style.coolPurple, R.style.coolGreen, R.style.coolBlack)
        val currentThemeNav = arrayOf(R.style.coolPinkNav, R.style.coolBlueNav, R.style.coolPurpleNav, R.style.coolGreenNav, R.style.coolBlackNav)

        val currentGradient = arrayOf(R.drawable.gradient_pink, R.drawable.gradient_blue, R.drawable.gradient_purple, R.drawable.gradient_green, R.drawable.gradient_black)

        var sort:Int=0
        //добавить: по папкам, по количеству прослушиваний
        var sortingList= arrayOf(MediaStore.Audio.Media.DATE_ADDED+ " DESC",MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.SIZE+" DESC")



    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var themeEditor=getSharedPreferences("THEMES", MODE_PRIVATE)
        themeIndex=themeEditor.getInt("theme",0)
        setTheme(currentThemeNav[themeIndex])
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

try {


        if(requestRuntimePermission()) {
            initialization()
            //перезапись
            FavouritesActivity.favSong = ArrayList()
            val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
            val jsonString = editor.getString("FavouriteSongs", null)
            val typeToken = object : TypeToken<ArrayList<Music>>(){}.type
            if(jsonString != null){
                val data: ArrayList<Music> = GsonBuilder().create().fromJson(jsonString, typeToken)
                FavouritesActivity.favSong.addAll(data)
            }
            PlaylistsActivity.musicPlaylist = PlaylistMusic()
            val jsonStringPL = editor.getString("MusicPlaylist", null)
            if(jsonStringPL != null){
                val dataPL: PlaylistMusic = GsonBuilder().create().fromJson(jsonStringPL, PlaylistMusic::class.java)
                PlaylistsActivity.musicPlaylist=dataPL
            }
        }
        binding.toolbar.setTitle("Все композиции")
        Navigation()

        //кнопка "случайное"
        binding.shuffleBTN.setOnClickListener {
            val bottomSheet = PlayerFragment.newInstance()
            val bundle = Bundle()
            bundle.putInt("index", 0)
            bundle.putString("class", "MainActivity")
            bottomSheet.arguments = bundle
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }
        //

        binding.refreshLayout.setOnRefreshListener {
            MusicListMA = getAllAudio()
            musicAdapter.updateMusicList(MusicListMA)

            binding.refreshLayout.isRefreshing = false
        }
}
catch (ex:Exception){
    Toast.makeText(this,ex.toString(),Toast.LENGTH_SHORT).show()
}
    }

    override fun onDestroy() {
        super.onDestroy()
        if(PlayerFragment.isPlaying && PlayerFragment.musicService != null){
            exitApp()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()
        //получение данных
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouritesActivity.favSong)
        editor.putString("FavouriteSongs", jsonString)

        val jsonStringPL = GsonBuilder().create().toJson(PlaylistsActivity.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPL)
        editor.apply()

        //сортировка
        var sortEditor=getSharedPreferences("SORTING", MODE_PRIVATE)
        var sortValue=sortEditor.getInt("sortOrder",0)
        if(sort!=sortValue){
            sort= sortValue
            MusicListMA = getAllAudio()
            musicAdapter.updateMusicList(MusicListMA)
        }
    }


    @RequiresApi(Build.VERSION_CODES.R)
    @SuppressLint("SetTextI18n")
    private fun initialization(){

        //поиск
        search=false

        var sortEditor=getSharedPreferences("SORTING", MODE_PRIVATE)
        sort=sortEditor.getInt("sortOrder",0)

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
        val selection = MediaStore.Audio.Media.IS_MUSIC +  " != 0"
        val projection = arrayOf(MediaStore.Audio.Media._ID,MediaStore.Audio.Media.TITLE,MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST,MediaStore.Audio.Media.DURATION,MediaStore.Audio.Media.DATE_ADDED, MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.ALBUM_ID)
        val cursor = this.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,selection,null, sortingList[sort], null)

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
        drawer = findViewById(R.id.Drawer)
        var toolbar=findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        var navView=findViewById<NavigationView>(R.id.NavView)
        val headerView = navView.getHeaderView(0)
        val linearHeader = headerView.findViewById<LinearLayout>(R.id.linearHeader)

        if (linearHeader == null) {
            val mainLayout = findViewById<LinearLayout>(R.id.linearHeader)
            mainLayout?.setBackgroundResource(currentGradient[themeIndex])
        } else {
            linearHeader.setBackgroundResource(currentGradient[themeIndex])
        }
        navView.setNavigationItemSelectedListener(this)
        toggle= ActionBarDrawerToggle(this,drawer,toolbar,R.string.o,R.string.c)
        drawer?.addDrawerListener(toggle)
        toggle.syncState()


    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.IDSignOut -> {
                Toast.makeText(this, "IDSignOut", Toast.LENGTH_SHORT).show()
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Выход")
                builder.setMessage("Вы действительно хотите выйти?")
                builder.setPositiveButton("Да", { dialogInterface, i -> exitApp()
                    })
                builder.setNegativeButton("Нет", { dialogInterface, i -> })
                builder.show()
            }

            R.id.IDMusicStorage -> {
                return false
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
    override fun onBackPressed() {
        super.onBackPressed()
        if (drawer?.isDrawerOpen(GravityCompat.START)!!) {
            drawer?.closeDrawer(GravityCompat.START)
        } else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view_menu, menu)
        findViewById<LinearLayout>(R.id.toolbar)
        var sw=menu?.findItem(R.id.searchView)?.actionView as SearchView
        sw.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean{
                sw.clearFocus()
                sw.setQuery("",false)
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                MusicListSearch= ArrayList()
                if (newText!=null){
                    val input=newText.lowercase()
                    for(song in MusicListMA) {
                        if (song.title.lowercase().contains(input)||song.artist.lowercase().contains(input)||song.album.lowercase().contains(input)) {
                            MusicListSearch.add(song)
                        }
                        search=true
                        musicAdapter.updateMusicList(MusicListSearch)
                    }
                }
                return true
            }

        })
        return super.onCreateOptionsMenu(menu)
    }
}