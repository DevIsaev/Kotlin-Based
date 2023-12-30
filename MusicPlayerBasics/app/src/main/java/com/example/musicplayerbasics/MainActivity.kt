package com.example.musicplayerbasics

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.musicplayerbasics.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding:ActivityMainBinding

    var drawer: DrawerLayout? = null

    private lateinit var permissionLancher:ActivityResultLauncher<Array<String>>
    private var isReadPermission=false
    private var isWritePermission=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        permissionLancher=registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){permissions->
            isReadPermission=permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE]?:isReadPermission
            isWritePermission=permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE]?:isWritePermission
        }
        requestPermission()

        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Navigation()

        binding.favouritesBTN.setOnClickListener {
            showBottomSheet()
        }
        binding.shuffleBTN.setOnClickListener {
            val itent=Intent(this,PlayerActivity::class.java)
            startActivity(itent)
        }
        binding.playlistsBTN.setOnClickListener {
            val itent=Intent(this,PlaylistsActivity::class.java)
            startActivity(itent)
        }
    }
    //запрос разрешения
    private fun requestPermission(){
        isWritePermission=ContextCompat.checkSelfPermission(this,android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
        isReadPermission=ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED

        val permissionRequest:MutableList<String> =ArrayList()
        if(!isReadPermission){
            permissionRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if(!isWritePermission){
            permissionRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if(permissionRequest.isNotEmpty()){
            permissionLancher.launch(permissionRequest.toTypedArray())
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
                showBottomSheet()
            }

            R.id.IDMusicStorage -> {
                Toast.makeText(this, "IDMusicStorage", Toast.LENGTH_SHORT).show()
            }

            R.id.IDMusicDownload -> {
                Toast.makeText(this, "IDMusicDownload", Toast.LENGTH_SHORT).show()
            }

            R.id.IDStore -> {
                Toast.makeText(this, "IDStore", Toast.LENGTH_SHORT).show()
            }

            R.id.IDPlaylists -> {
                Toast.makeText(this, "IDPlaylists", Toast.LENGTH_SHORT).show()
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
    //sheet
    private fun showBottomSheet(){
//       val dialogview=layoutInflater.inflate(R.layout.activity_player,null)
//        dialog=BottomSheetDialog(this,R.style.BottomSheetTheme)
//        dialog.setContentView(dialogview)
//        dialog.show()

    val bottomsheet=PlayerFragment()
    bottomsheet.show(supportFragmentManager,"BottomSheetDialog")
    }
}