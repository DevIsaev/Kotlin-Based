package com.example.a02swetofor

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import kotlinx.coroutines.delay
import java.util.Timer
import java.util.TimerTask

class MainActivity : Activity() {
    var timer=Timer()
    var counter = 0
    var imSwetofor:ImageView?=null
    var arrColors:IntArray= intArrayOf(R.drawable.semafor_red,R.drawable.semafor_yellow,R.drawable.semafor_green)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imSwetofor=findViewById(R.id.swetofor)
        var button=findViewById<ImageView>(R.id.bGo)
        var isRun=false




        button.setOnClickListener {
            if(!isRun){
                StartTimer()
                button.setImageResource(R.drawable.button_stop)
                isRun=true
            }
            else{
                button.setImageResource(R.drawable.button_start)
                imSwetofor?.setImageResource(R.drawable.semafor_grey)
                timer.cancel()
                isRun=false
                counter=0
            }
        }

    }
    fun StartTimer(){
        timer?.schedule(object :TimerTask(){
            override fun run() {
                runOnUiThread {
                    imSwetofor?.setImageResource(arrColors[counter])
                    counter++
                    if (counter == 2) {
                        counter = 0
                    }
                }
            }

        },100)
    }
}