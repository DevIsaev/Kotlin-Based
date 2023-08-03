package com.example.a03reader

import android.content.res.TypedArray
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.NavView
import kotlinx.android.synthetic.main.main_content.rcView
import java.lang.Exception

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var adapterChange:MyAdapter?=null
    var drawer:DrawerLayout?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        NavView.setNavigationItemSelectedListener(this)

        drawer=findViewById(R.id.Drawer)
        var l=ArrayList<ListItem>()
        l.addAll(FillArrays(resources.getStringArray(R.array.fish),resources.getStringArray(R.array.fish_content),resources.getStringArray(R.array.fish_text),GetImageId(R.array.fish_img)))

        rcView.hasFixedSize()
        rcView.layoutManager=LinearLayoutManager(this)
        adapterChange= MyAdapter(l,this)
        rcView.adapter=adapterChange

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.IDFish ->{
                Toast.makeText(this,"test",Toast.LENGTH_SHORT).show()
                adapterChange?.UpdateAdapter((FillArrays(resources.getStringArray(R.array.fish),resources.getStringArray(R.array.fish_content),resources.getStringArray(R.array.fish_text),GetImageId(R.array.fish_img))))
            }
            R.id.IDNajiv->{
                Toast.makeText(this,"test",Toast.LENGTH_SHORT).show()
                adapterChange?.UpdateAdapter((FillArrays(resources.getStringArray(R.array.najivki),resources.getStringArray(R.array.najivki_content),resources.getStringArray(R.array.najivki_text),GetImageId(R.array.najivki_img))))
            }
            R.id.IDSnasty->{
                    Toast.makeText(this, "test", Toast.LENGTH_SHORT).show()
                    adapterChange?.UpdateAdapter(
                        (FillArrays(
                            resources.getStringArray(R.array.snasty),
                            resources.getStringArray(R.array.snasty_content),
                            resources.getStringArray(R.array.snasty_text),
                            GetImageId(R.array.snasty_img)
                        ))
                    )
            }
            R.id.IDStories->{
                Toast.makeText(this,"test",Toast.LENGTH_SHORT).show()
                adapterChange?.UpdateAdapter((FillArrays(resources.getStringArray(R.array.stories),resources.getStringArray(R.array.stories_content),resources.getStringArray(R.array.stories_text),GetImageId(R.array.stories_img))))
            }
        }
        drawer?.closeDrawer(GravityCompat.START)
        return true
    }


    fun GetImageId(imageArrId:Int):IntArray{
        var tArr:TypedArray=resources.obtainTypedArray(imageArrId)
        var count=tArr.length()
        var ids=IntArray(count)
        for (id in ids.indices){
            ids[id]=tArr.getResourceId(id,0)
        }
        tArr.recycle()
        return ids
    }
    fun FillArrays(titleArr:Array<String>, descArr:Array<String>,
                   txtArr:Array<String>, imgArr:IntArray):List<ListItem> {

        var listItemArr = ArrayList<ListItem>()

        for (n in 0..titleArr.size-1) {
            var lItem = ListItem(imgArr[n], titleArr[n], descArr[n], txtArr[n])
            listItemArr.add(lItem)
        }
        return  listItemArr
    }
}