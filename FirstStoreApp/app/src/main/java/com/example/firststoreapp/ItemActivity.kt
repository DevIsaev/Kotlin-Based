package com.example.firststoreapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

class ItemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        val title=findViewById<TextView>(R.id.ITitle)
        val desc=findViewById<TextView>(R.id.IDesc)
        val text=findViewById<TextView>(R.id.IText)
        val button=findViewById<Button>(R.id.IButton)
        val img=findViewById<ImageView>(R.id.ItemImage)

        title.text=intent.getStringExtra("ITitle")
        desc.text=intent.getStringExtra("IDesc")
        text.text=intent.getStringExtra("IText")
        val imageResId = intent.getIntExtra("IImageResId",R.drawable.ps2)
        img.setImageResource(imageResId)


        button.setOnClickListener {
            val intent= Intent(this,ItemsActivity::class.java)
            startActivity(intent)
        }
    }
}