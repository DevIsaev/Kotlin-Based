package com.example.a03reader

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.w3c.dom.Text

class ItemActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_page)

        var titleTV=findViewById<TextView>(R.id.titleItem)
        var imgIV=findViewById<ImageView>(R.id.imgItem)
        var descTV=findViewById<TextView>(R.id.descItem)
        var textTV=findViewById<TextView>(R.id.textItem)

        titleTV.text=intent.getStringExtra("Title")
        descTV.text=intent.getStringExtra("Desc")
        textTV.text=intent.getStringExtra("Text")
        imgIV.setImageResource(intent.getIntExtra("Image",R.drawable.som))
    }
}