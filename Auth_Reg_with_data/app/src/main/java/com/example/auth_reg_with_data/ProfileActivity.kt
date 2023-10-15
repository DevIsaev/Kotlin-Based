package com.example.auth_reg_with_data

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_profile.NavView


class ProfileActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    private lateinit var auth: FirebaseAuth
    var drawer: DrawerLayout?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        NavView.setNavigationItemSelectedListener(this)
        drawer=findViewById(R.id.Drawer)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.IDSignOut ->{
                Toast.makeText(this,"IDSignOut", Toast.LENGTH_SHORT).show()
                //adapterChange?.UpdateAdapter((FillArrays(resources.getStringArray(R.array.fish),resources.getStringArray(R.array.fish_content),resources.getStringArray(R.array.fish_text),GetImageId(R.array.fish_img))))
            }
            R.id.IDMusicStorage->{
                Toast.makeText(this,"IDMusicStorage", Toast.LENGTH_SHORT).show()
                //adapterChange?.UpdateAdapter((FillArrays(resources.getStringArray(R.array.najivki),resources.getStringArray(R.array.najivki_content),resources.getStringArray(R.array.najivki_text),GetImageId(R.array.najivki_img))))
            }
            R.id.IDMusicDownload->{
                Toast.makeText(this, "IDMusicDownload", Toast.LENGTH_SHORT).show()
                //adapterChange?.UpdateAdapter((FillArrays( resources.getStringArray(R.array.snasty), resources.getStringArray(R.array.snasty_content), resources.getStringArray(R.array.snasty_text), GetImageId(R.array.snasty_img) )) )
            }
            R.id.IDStore->{
                Toast.makeText(this,"IDStore", Toast.LENGTH_SHORT).show()
                //adapterChange?.UpdateAdapter((FillArrays(resources.getStringArray(R.array.stories),resources.getStringArray(R.array.stories_content),resources.getStringArray(R.array.stories_text),GetImageId(R.array.stories_img))))
            }
            R.id.IDPlaylists->{
                Toast.makeText(this,"IDPlaylists", Toast.LENGTH_SHORT).show()
                //adapterChange?.UpdateAdapter((FillArrays(resources.getStringArray(R.array.stories),resources.getStringArray(R.array.stories_content),resources.getStringArray(R.array.stories_text),GetImageId(R.array.stories_img))))
            }
            R.id.IDStatistic->{
                Toast.makeText(this,"IDStatistic", Toast.LENGTH_SHORT).show()
                //adapterChange?.UpdateAdapter((FillArrays(resources.getStringArray(R.array.stories),resources.getStringArray(R.array.stories_content),resources.getStringArray(R.array.stories_text),GetImageId(R.array.stories_img))))
            }
            R.id.IDSettings->{
                Toast.makeText(this,"IDSettings", Toast.LENGTH_SHORT).show()
                //adapterChange?.UpdateAdapter((FillArrays(resources.getStringArray(R.array.stories),resources.getStringArray(R.array.stories_content),resources.getStringArray(R.array.stories_text),GetImageId(R.array.stories_img))))
            }
            R.id.IDPlugIn->{
                Toast.makeText(this,"IDPlugIn", Toast.LENGTH_SHORT).show()
                //adapterChange?.UpdateAdapter((FillArrays(resources.getStringArray(R.array.stories),resources.getStringArray(R.array.stories_content),resources.getStringArray(R.array.stories_text),GetImageId(R.array.stories_img))))
            }
            R.id.ID_F_A_Q->{
                Toast.makeText(this,"ID_F_A_Q", Toast.LENGTH_SHORT).show()
                //adapterChange?.UpdateAdapter((FillArrays(resources.getStringArray(R.array.stories),resources.getStringArray(R.array.stories_content),resources.getStringArray(R.array.stories_text),GetImageId(R.array.stories_img))))
            }
            R.id.IDAbout->{
                Toast.makeText(this,"IDAbout", Toast.LENGTH_SHORT).show()
                //adapterChange?.UpdateAdapter((FillArrays(resources.getStringArray(R.array.stories),resources.getStringArray(R.array.stories_content),resources.getStringArray(R.array.stories_text),GetImageId(R.array.stories_img))))
            }
        }
        drawer?.closeDrawer(GravityCompat.START)
        return true
    }
}
