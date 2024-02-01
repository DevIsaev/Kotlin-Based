package com.example.musicplayerbasics

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.example.musicplayerbasics.databinding.ActivityFavouritesBinding
import com.google.android.material.navigation.NavigationView
import com.google.gson.GsonBuilder

class FavouritesActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityFavouritesBinding
    private lateinit var adapter: AdapterMusicListFavourite
    var drawer: DrawerLayout? = null
    lateinit var toggle: ActionBarDrawerToggle
    companion object {
        var favouritesChanged: Boolean = false

        var favSong:ArrayList<Music> = ArrayList()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        binding= ActivityFavouritesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            favSong = playlistCheck(favSong)

            binding.favouriteRV.setHasFixedSize(true)
            binding.favouriteRV.setItemViewCacheSize(13)
            binding.favouriteRV.layoutManager = GridLayoutManager(this, 3)
            adapter = AdapterMusicListFavourite(this, favSong)
            binding.favouriteRV.adapter = adapter

            favouritesChanged = false

            if (favSong.size < 1) {
                binding.shuffleBtn.visibility = View.INVISIBLE
            }
            binding.shuffleBtn.setOnClickListener {
                val bottomSheet = PlayerFragment.newInstance()
                val bundle = Bundle()
                bundle.putInt("index", 0)
                bundle.putString("class", "FavouriteShuffle")
                bottomSheet.arguments = bundle
                bottomSheet.show(supportFragmentManager, bottomSheet.tag)
            }
            binding.refreshLayout.setOnRefreshListener {
                adapter.updateFavourites(favSong)
                favouritesChanged = false

                binding.refreshLayout.isRefreshing = false
            }
            binding.toolbar.setTitle("Избранное")
            Navigation()
        }
        catch (ex:Exception){
            Toast.makeText(this,ex.toString(),Toast.LENGTH_SHORT).show()
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
            mainLayout?.setBackgroundResource(MainActivity.currentGradient[MainActivity.themeIndex])
        } else {
            linearHeader.setBackgroundResource(MainActivity.currentGradient[MainActivity.themeIndex])
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
            }

            R.id.IDMusicStorage -> {
                val itent= Intent(this,MainActivity::class.java)
                startActivity(itent)
            }

            R.id.IDMusicFavourite -> {
                return false
            }
            R.id.IDMusicDownload -> {
                Toast.makeText(this, "IDMusicDownload", Toast.LENGTH_SHORT).show()
            }

            R.id.IDStore -> {
                Toast.makeText(this, "IDStore", Toast.LENGTH_SHORT).show()
            }

            R.id.IDPlaylists -> {
                val itent= Intent(this,PlaylistsActivity::class.java)
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
    override fun onResume() {
        super.onResume()
        //сохранение
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouritesActivity.favSong)
        editor.putString("FavouriteSongs", jsonString)

        val jsonStringPL = GsonBuilder().create().toJson(PlaylistsActivity.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPL)
        editor.apply()

        if(favouritesChanged) {
            adapter.updateFavourites(favSong)
            favouritesChanged = false
        }
    }


}