package com.example.musicplayerbasics

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.musicplayerbasics.databinding.ActivityFavouritesBinding
import com.google.android.material.navigation.NavigationView

class FavouritesActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityFavouritesBinding
    var drawer: DrawerLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityFavouritesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTheme(R.style.coolPinkNav)
        Navigation()
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
                val itent= Intent(this,PlaylistsActivity::class.java)
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
}