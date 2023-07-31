package com.example.a01begin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast

class MainActivity3 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main3)

        var list=findViewById<ListView>(R.id.LV)

            //var NAMES= listOf("DTS","DOLBY DIGITAL","WAV","FLAC","MP3","ACC")
        var NameList=ArrayList<Any>()
        NameList.add("DOLBY DIGITAL")
        NameList.add("DTS")
        NameList.add(3)
        NameList.add("WAV")
        NameList.add("FLAC")
        NameList.add("MP3")
        NameList.add(5)
        NameList.add("ACC")

        list.adapter=ArrayAdapter(this,android.R.layout.simple_list_item_1,NameList)

        list.setOnItemClickListener{parent,view,position,id->
            Toast.makeText(this,"Pressed - ${(position)+1}, ${NameList.get(position)}",Toast.LENGTH_SHORT).show()
        }

    }
}