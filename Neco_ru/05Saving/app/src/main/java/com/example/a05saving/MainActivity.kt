package com.example.a05saving

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    var count=0
    var count2="23"
    var save:SharedPreferences?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var score=findViewById<TextView>(R.id.tvScore)
        var score2=findViewById<TextView>(R.id.tvScore2)
        var bPlus=findViewById<Button>(R.id.bPlus)

        //
        save=getSharedPreferences("TABLE", Context.MODE_PRIVATE)
        //
        //
        count=save?.getInt("SCORE",0)!!
        count2=save?.getString("SCORE2","23")!!
        //
        score.text=count.toString()
        score2.text=count2.toString()


        bPlus.setOnClickListener {
            count++
            score.text=count.toString()
            Save(count)
        }
    }
    fun Save(res:Int){
        val editor=save?.edit()

        editor?.putInt("SCORE",res)
        editor?.putString("SCORE2","Test")
        editor?.apply()
    }
    fun deleteItem(view: View){
DeleteElement("SCORE2")
    }
    fun deleteAll(view: View){
        DeleteAll()
    }

    fun DeleteAll(){
        val editor=save?.edit()
        editor?.clear()
        editor?.apply()
        count=0
    }
    fun DeleteElement(item:String){
        val editor=save?.edit()
        editor?.remove(item)
        editor?.apply()
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        Save(count)
//    }
}