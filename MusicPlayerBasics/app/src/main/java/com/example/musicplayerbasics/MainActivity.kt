package com.example.musicplayerbasics

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.example.musicplayerbasics.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.qamar.curvedbottomnaviagtion.CurvedBottomNavigation


class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding

    var drawer: DrawerLayout? = null
    lateinit var toggle: ActionBarDrawerToggle
    companion object {
        lateinit var MusicListMA: ArrayList<Music>
        lateinit var musicAdapter: AdapterMusicList
        lateinit var MusicListSearch: ArrayList<Music>
        var search: Boolean = false

        var themeIndex: Int = 0
        val currentTheme = arrayOf(
            R.style.coolPink,
            R.style.coolBlue,
            R.style.coolPurple,
            R.style.coolGreen,
            R.style.coolBlack,
            R.style.coolCustom1,
            R.style.coolCustom2,
            R.style.coolCustom3,
            R.style.coolCustom4,
            R.style.coolCustom5,
        )
        val currentThemeNav = arrayOf(
            R.style.coolPinkNav,
            R.style.coolBlueNav,
            R.style.coolPurpleNav,
            R.style.coolGreenNav,
            R.style.coolBlackNav,
            R.style.coolCustom1Nav,
            R.style.coolCustom2Nav,
            R.style.coolCustom3Nav,
            R.style.coolCustom4Nav,
            R.style.coolCustom5Nav,
        )
        val currentGradient = arrayOf(
            R.drawable.gradient_pink,
            R.drawable.gradient_blue,
            R.drawable.gradient_purple,
            R.drawable.gradient_green,
            R.drawable.gradient_black,
            R.drawable.gradient1,
            R.drawable.gradient2,
            R.drawable.gradient3,
            R.drawable.gradient4,
            R.drawable.gradient5,
        )
        var sort: Int = 0

        //добавить: по папкам, по количеству прослушиваний
        var sortingList = arrayOf(
            MediaStore.Audio.Media.DATE_ADDED + " DESC",
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.SIZE + " DESC"
        )
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var themeEditor = getSharedPreferences("THEMES", MODE_PRIVATE)
        themeIndex = themeEditor.getInt("theme", 0)
        setTheme(currentThemeNav[themeIndex])
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        try {
            BottomNavigation()
            if (requestRuntimePermission()) {
                //перезапись
                FavouritesFragment.favSong = ArrayList()
                val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE)
                val jsonString = editor.getString("FavouriteSongs", null)
                val typeToken = object : TypeToken<ArrayList<Music>>() {}.type
                if (jsonString != null) {
                    val data: ArrayList<Music> =
                        GsonBuilder().create().fromJson(jsonString, typeToken)
                    FavouritesFragment.favSong.addAll(data)
                }
                PlaylistsFragment.musicPlaylist = PlaylistMusic()
                val jsonStringPL = editor.getString("MusicPlaylist", null)
                if (jsonStringPL != null) {
                    val dataPL: PlaylistMusic =
                        GsonBuilder().create().fromJson(jsonStringPL, PlaylistMusic::class.java)
                    PlaylistsFragment.musicPlaylist = dataPL
                }
            }
            Navigation()
        } catch (ex: Exception) {
            binding.toolbar.setTitle(ex.toString())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (PlayerFragment.isPlaying && PlayerFragment.musicService != null) {
            exitApp()
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onResume() {
        super.onResume()
        //получение данных
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouritesFragment.favSong)
        editor.putString("FavouriteSongs", jsonString)

        val jsonStringPL = GsonBuilder().create().toJson(PlaylistsFragment.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPL)
        editor.apply()

        //сортировка
        var sortEditor = getSharedPreferences("SORTING", MODE_PRIVATE)
        var sortValue = sortEditor.getInt("sortOrder", 0)
        if (sort != sortValue) {
            sort = sortValue
            var almf=AllMusicFragment()
            MusicListMA = almf.getAllAudio()
            musicAdapter.updateMusicList(MusicListMA)
        }
        if(PlayerFragment.musicService != null){
            var nowPlaying=findViewById<FragmentContainerView>(R.id.nowPlaying)
            nowPlaying.visibility= View.VISIBLE
        }
    }


    // запрос разрешения
    private fun requestRuntimePermission(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE
                    ),
                    13
                )
                return true
            }
        }
        // android 13 permission request
        else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_MEDIA_AUDIO
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_MEDIA_AUDIO),
                    13
                )
                return true
            }
        }
        return true
    }

        //Navigation drawer
        private fun Navigation() {
            drawer = findViewById(R.id.Drawer)
            var toolbar = findViewById<Toolbar>(R.id.toolbar)
            setSupportActionBar(toolbar)
            var navView = findViewById<NavigationView>(R.id.NavView)
            val headerView = navView.getHeaderView(0)
            val linearHeader = headerView.findViewById<LinearLayout>(R.id.linearHeader)

            if (linearHeader == null) {
                val mainLayout = findViewById<LinearLayout>(R.id.linearHeader)
                mainLayout?.setBackgroundResource(currentGradient[themeIndex])
            } else {
                linearHeader.setBackgroundResource(currentGradient[themeIndex])
            }
            navView.setNavigationItemSelectedListener(this)
            toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.o, R.string.c)
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
                    builder.setPositiveButton("Да", { dialogInterface, i ->
                        exitApp()
                    })
                    builder.setNegativeButton("Нет", { dialogInterface, i -> })
                    builder.show()
                }

                R.id.IDMusicStorage -> {
                    return false
                }

                R.id.IDMusicFavourite -> {
                    val itent = Intent(this, FavouritesActivity::class.java)
                    startActivity(itent)
                }

                R.id.IDMusicDownload -> {
                    Toast.makeText(this, "IDMusicDownload", Toast.LENGTH_SHORT).show()
                }

                R.id.IDStore -> {
                    Toast.makeText(this, "IDStore", Toast.LENGTH_SHORT).show()
                }

                R.id.IDPlaylists -> {
                    val itent = Intent(this, PlaylistsActivity::class.java)
                    startActivity(itent)
                }

                R.id.IDStatistic -> {
                    Toast.makeText(this, "IDStatistic", Toast.LENGTH_SHORT).show()
                }

                R.id.IDSettings -> {
                    val itent = Intent(this, Settings::class.java)
                    startActivityForResult(itent,SETTINGS_REQUEST_CODE)
                }

                R.id.IDPlugIn -> {
                    Toast.makeText(this, "IDPlugIn", Toast.LENGTH_SHORT).show()
                }

                R.id.ID_F_A_Q -> {
                    Toast.makeText(this, "ID_F_A_Q", Toast.LENGTH_SHORT).show()
                }

                R.id.IDAbout -> {
                    val itent = Intent(this, About::class.java)
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
            var sw = menu?.findItem(R.id.searchView)?.actionView as SearchView
            sw.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    sw.clearFocus()
                    sw.setQuery("", false)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    MusicListSearch = ArrayList()
                    if (newText != null) {
                        val input = newText.lowercase()
                        for (song in MusicListMA) {
                            if (song.title.lowercase().contains(input) || song.artist.lowercase()
                                    .contains(input) || song.album.lowercase().contains(input)
                            ) {
                                MusicListSearch.add(song)
                            }
                            search = true
                            musicAdapter.updateMusicList(MusicListSearch)
                        }
                    }
                    return true
                }

            })
            return super.onCreateOptionsMenu(menu)
        }


    val SETTINGS_REQUEST_CODE = 1 // Уникальный код запроса
    // Метод обработки результата обратного вызова из SettingsActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Обновите тему или выполните другие действия при изменении настроек в SettingsActivity
            finishAffinity() // Пересоздаем Activity, чтобы применить новую тему
        }
    }

    fun BottomNavigation(){
        var bottomNavigation=findViewById<CurvedBottomNavigation>(R.id.bottomNavigation)
        bottomNavigation.add(CurvedBottomNavigation.Model(1,"", R.drawable.baseline_favorite_242))
        bottomNavigation.add(CurvedBottomNavigation.Model(2,"", R.drawable.baseline_house_24))
        bottomNavigation.add(CurvedBottomNavigation.Model(3,"", R.drawable.baseline_library_music_24))

        bottomNavigation.setOnClickMenuListener {
            when(it.id){
                1->{
                    //replaceFragment(First())
                    binding.toolbar.setTitle("Избранное")
                    replaceFragment(FavouritesFragment())
                    //Toast.makeText(this,it.id.toString(),Toast.LENGTH_SHORT).show()
                }
                2->{
                    //replaceFragment(Second())
                    binding.toolbar.setTitle("Все композиции")
                    replaceFragment(AllMusicFragment())
                    //Toast.makeText(this,it.id.toString(),Toast.LENGTH_SHORT).show()
                }
                3->{
                    //replaceFragment(Third())
                    binding.toolbar.setTitle("Плейлисты")
                    replaceFragment(PlaylistsFragment())
                    //Toast.makeText(this,it.id.toString(),Toast.LENGTH_SHORT).show()
                }
            }
        }

        replaceFragment(AllMusicFragment())
        binding.toolbar.setTitle("Все композиции")
        bottomNavigation.show(2)
    }
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container_view_tag,fragment)
            .commit()
    }
}