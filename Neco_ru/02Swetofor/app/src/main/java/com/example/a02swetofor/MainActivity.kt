package com.example.a02swetofor

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import kotlinx.coroutines.delay
import java.util.Timer
import java.util.TimerTask

class MainActivity : Activity() {
    var timer:Timer?=null
    var arrColors:IntArray= intArrayOf(R.drawable.semafor_red,R.drawable.semafor_yellow,R.drawable.semafor_green)
    var counter = 0
    var isStart=false
    var imSwetofor:ImageView?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imSwetofor=findViewById(R.id.swetofor)
    }
    fun onClickStartStop(view:View){
        view as ImageButton
        if (!isStart){
            timer=Timer()
            StartStop()
            view.setImageResource(R.drawable.button_stop)
            isStart=true
        }
        else{
            imSwetofor?.setImageResource(R.drawable.semafor_grey)
            view.setImageResource(R.drawable.button_start)
            timer?.cancel()
            isStart=false
            counter=0
        }

    }
    fun StartStop(){
        timer=Timer()
        timer?.schedule(object :TimerTask(){

            override fun run() {
                runOnUiThread {
                    imSwetofor?.setImageResource(arrColors[counter])
                    counter++
                    if (counter == 3)counter = 0

                }
            }

        },0,1000)
    }
}