package com.example.auth_reg_with_data

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth



class ProfileActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var auth: FirebaseAuth
    var drawer: DrawerLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        auth = FirebaseAuth.getInstance()
        var fieldname=findViewById<TextView>(R.id.tvNameId)
        fieldname.text=DataManager.userName
        var fieldemail=findViewById<TextView>(R.id.tvEmailId)
        fieldemail.text=DataManager.userEmail
        var avatar=findViewById<ImageView>(R.id.Avatar)
        var url=DataManager.userImageUrl.toString()
        if (!url.isNullOrEmpty()) {
            val options = RequestOptions().format(DecodeFormat.PREFER_ARGB_8888)
            Glide.with(this)
                .asDrawable()
                .error(R.drawable.avatar)
                .apply(options)
                .load(url)
                .into(avatar)
        }
        Navigation()

    }
    fun SignOut() {

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Выход")
        builder.setMessage("Вы действительно хотите выйти?")
        builder.setPositiveButton("Да",
            DialogInterface.OnClickListener { dialogInterface, i ->
                auth.signOut()
                DataManager.userEmail="НЕ АВТОРИЗОВАН"
                DataManager.userName="НЕ АВТОРИЗОВАН"
                DataManager.userImageUrl=null
                startActivity(Intent(this, Auth::class.java))
                finish()
            })
        builder.setNegativeButton("Нет",
            DialogInterface.OnClickListener { dialogInterface, i -> })
        builder.show()
    }

    fun Navigation(){
        var NavView=findViewById<NavigationView>(R.id.NavView)
        NavView.setNavigationItemSelectedListener(this)
        drawer = findViewById(R.id.Drawer)
        NavView.getHeaderView(0).findViewById<TextView>(R.id.UserNameHead).text=DataManager.userName
        NavView.getHeaderView(0).findViewById<TextView>(R.id.EmailHead).text=DataManager.userEmail
        if (!DataManager.userImageUrl.toString().isNullOrEmpty()) {
            val options = RequestOptions().format(DecodeFormat.PREFER_ARGB_8888)
            Glide.with(this)
                .asDrawable()
                .error(R.drawable.avatar)
                .apply(options)
                .load(DataManager.userImageUrl.toString())
                .into(NavView.getHeaderView(0).findViewById(R.id.circleAvatar))
        }
    }
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.IDSignOut -> {
                Toast.makeText(this, "IDSignOut", Toast.LENGTH_SHORT).show()
                SignOut()
                //adapterChange?.UpdateAdapter((FillArrays(resources.getStringArray(R.array.fish),resources.getStringArray(R.array.fish_content),resources.getStringArray(R.array.fish_text),GetImageId(R.array.fish_img))))
            }

            R.id.IDMusicStorage -> {
                Toast.makeText(this, "IDMusicStorage", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Music::class.java)
                startActivity(intent)
                //adapterChange?.UpdateAdapter((FillArrays(resources.getStringArray(R.array.najivki),resources.getStringArray(R.array.najivki_content),resources.getStringArray(R.array.najivki_text),GetImageId(R.array.najivki_img))))
            }

            R.id.IDMusicDownload -> {
                Toast.makeText(this, "IDMusicDownload", Toast.LENGTH_SHORT).show()
                //adapterChange?.UpdateAdapter((FillArrays( resources.getStringArray(R.array.snasty), resources.getStringArray(R.array.snasty_content), resources.getStringArray(R.array.snasty_text), GetImageId(R.array.snasty_img) )) )
            }

            R.id.IDStore -> {
                Toast.makeText(this, "IDStore", Toast.LENGTH_SHORT).show()
                //adapterChange?.UpdateAdapter((FillArrays(resources.getStringArray(R.array.stories),resources.getStringArray(R.array.stories_content),resources.getStringArray(R.array.stories_text),GetImageId(R.array.stories_img))))
            }

            R.id.IDPlaylists -> {
                Toast.makeText(this, "IDPlaylists", Toast.LENGTH_SHORT).show()
                //adapterChange?.UpdateAdapter((FillArrays(resources.getStringArray(R.array.stories),resources.getStringArray(R.array.stories_content),resources.getStringArray(R.array.stories_text),GetImageId(R.array.stories_img))))
            }

            R.id.IDStatistic -> {
                Toast.makeText(this, "IDStatistic", Toast.LENGTH_SHORT).show()
                //adapterChange?.UpdateAdapter((FillArrays(resources.getStringArray(R.array.stories),resources.getStringArray(R.array.stories_content),resources.getStringArray(R.array.stories_text),GetImageId(R.array.stories_img))))
            }

            R.id.IDSettings -> {
                Toast.makeText(this, "IDSettings", Toast.LENGTH_SHORT).show()
                //adapterChange?.UpdateAdapter((FillArrays(resources.getStringArray(R.array.stories),resources.getStringArray(R.array.stories_content),resources.getStringArray(R.array.stories_text),GetImageId(R.array.stories_img))))
            }

            R.id.IDPlugIn -> {
                Toast.makeText(this, "IDPlugIn", Toast.LENGTH_SHORT).show()
                //adapterChange?.UpdateAdapter((FillArrays(resources.getStringArray(R.array.stories),resources.getStringArray(R.array.stories_content),resources.getStringArray(R.array.stories_text),GetImageId(R.array.stories_img))))
            }

            R.id.ID_F_A_Q -> {
                Toast.makeText(this, "ID_F_A_Q", Toast.LENGTH_SHORT).show()
                //adapterChange?.UpdateAdapter((FillArrays(resources.getStringArray(R.array.stories),resources.getStringArray(R.array.stories_content),resources.getStringArray(R.array.stories_text),GetImageId(R.array.stories_img))))
            }

            R.id.IDAbout -> {
                Toast.makeText(this, "IDAbout", Toast.LENGTH_SHORT).show()
                //adapterChange?.UpdateAdapter((FillArrays(resources.getStringArray(R.array.stories),resources.getStringArray(R.array.stories_content),resources.getStringArray(R.array.stories_text),GetImageId(R.array.stories_img))))
            }
        }
        drawer?.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        auth.signOut()
        finish()
    }
}
