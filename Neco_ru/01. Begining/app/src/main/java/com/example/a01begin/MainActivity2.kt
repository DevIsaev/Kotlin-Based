package com.example.a01begin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast

class MainActivity2 : AppCompatActivity() {
    var n1=56
    var n2=34
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        var n=56
        var str="в <магазине> <осталось> $n яблок, а может ${GetNumber()}"
        var text:TextView=findViewById(R.id.tView)
        var text2:TextView=findViewById(R.id.tView2)
        var text3:TextView=findViewById(R.id.tView3)

        var substring:String=str.substringAfter('<')
        var substring1:String=str.substringBefore('>')
        var substring2:String=substring.substringBefore('>')//без а
        text.setText(substring+"\n"+substring1+"\n"+substring2)

        var sum:Int=GetNumber()
        text2.setText(sum.toString())

        var substring3:String=str.substringAfter('<')
        var substring4:String=substring3.substringAfter('<')
        var substring5:String=substring4.substringBefore('>')//без а
        text2.setText(substring3+"\n"+substring4+"\n"+substring5)


        var str2="в <магазине> <осталось> $n яблок, а <может> ${GetNumber()}. <xp,> <de,trrtr>"
        var count=0
        for (i in str2.indices){
            if(str2.get(i)=='<'){
                count++
            }
        }
        var startPosition=IntArray(count)
        var endPosition=IntArray(count)
        var startEncounter=0
        var endEncounter=0
        for (i in str2.indices){
            if(str2.get(i)=='<'){
                startPosition[startEncounter]=i
                startEncounter++
            }
            if(str2.get(i)=='>'){
                endPosition[endEncounter]=i
                endEncounter++
            }
        }
        var found=Array(count){""}
        for (n in startPosition.indices){
            found[n]=str2.substring(startPosition[n]+1,endPosition[n])
            val show = Toast.makeText(this, found[n].toString(), Toast.LENGTH_SHORT).show()
        }

        //val show = Toast.makeText(this, found.toString(), Toast.LENGTH_SHORT).show()

        text3.setText(count.toString())




    }
    fun GetNumber():Int{
        return n1+n2
    }
}