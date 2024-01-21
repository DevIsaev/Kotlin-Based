package com.example.musicplayerbasics

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
    companion object {
        var favouritesChanged: Boolean = false

        var favSong:ArrayList<Music> = ArrayList()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        binding= ActivityFavouritesBinding.inflate(layoutInflater)
        setContentView(binding.root)


        favSong= playlistCheck(favSong)

        binding.favouriteRV.setHasFixedSize(true)
        binding.favouriteRV.setItemViewCacheSize(13)
        binding.favouriteRV.layoutManager = GridLayoutManager(this, 4)
        adapter = AdapterMusicListFavourite(this, favSong)
        binding.favouriteRV.adapter = adapter

        favouritesChanged = false

        if (favSong.size<1) {
            binding.shuffleBtn.visibility= View.INVISIBLE
        }
        binding.shuffleBtn.setOnClickListener {
            val bottomSheet = PlayerFragment.newInstance()
            val bundle = Bundle()
            bundle.putInt("index", 0)
            bundle.putString("class", "FavouriteShuffle")
            bottomSheet.arguments = bundle
            bottomSheet.show(supportFragmentManager, bottomSheet.tag)
        }
        Navigation()

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
                finish()
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

    override fun onResume() {
        super.onResume()
        //сохранение
        val editor = getSharedPreferences("FAVOURITES", MODE_PRIVATE).edit()
        val jsonString = GsonBuilder().create().toJson(FavouritesActivity.favSong)
        editor.putString("FavouriteSongs", jsonString)

        val jsonStringPL = GsonBuilder().create().toJson(PlaylistsActivity.musicPlaylist)
        editor.putString("MusicPlaylist", jsonStringPL)
        editor.apply()
    }


}