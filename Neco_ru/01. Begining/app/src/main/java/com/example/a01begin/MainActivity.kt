package com.example.a01begin

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity() {
    var n1=5
    var n2=34
    var ch='5'
    var start=true
    var text:TextView?=null
    var l:ConstraintLayout?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);
        var b:Button=findViewById(R.id.buttonTWO)
        var b2:Button=findViewById(R.id.buttonTHREE)
        var text:TextView=findViewById(R.id.textV)
        var l:ConstraintLayout=findViewById(R.id.cLayout)
        var number:Int=0
        text.setText(number.toString())

        var counter=0
        start=true
        Thread{
            while (start){
                Thread.sleep(1000)
                runOnUiThread {
                    if (counter == 10) {
                        l?.setBackgroundColor(Color.GREEN)
                    }
                    text?.setText(counter.toString())
                    counter++
                }
            }
        }.start()


        b.setOnClickListener {
            val intent = Intent(this, MainActivity2::class.java).apply {
                putExtra("передача","текст при переходе из другого активити")
            }
            startActivity(intent)
            onDestroy()
        }

        b2.setOnClickListener {
            val intent = Intent(this, MainActivity3::class.java)
            startActivity(intent)
            onDestroy()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        start=false
    }

}