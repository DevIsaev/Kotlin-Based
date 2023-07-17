package com.example.todo_list

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = findViewById<EditText>(R.id.userData)
        val button=findViewById<Button>(R.id.ButtonGo)
        val label:ListView=findViewById(R.id.List)

        val arr: MutableList<String> = mutableListOf()
        val adapter= ArrayAdapter(this, android.R.layout.simple_list_item_1,arr)
        label.adapter=adapter
        
label.setOnItemClickListener { adapterView, view, i, l ->
    val t=label.getItemAtPosition(i).toString()
    adapter.remove(t)
    Toast.makeText(this,"Удалено: $t",Toast.LENGTH_LONG).show()
}
        
        
        
        
        button.setOnClickListener {
        if (data.text.isNotEmpty()){
            val sdf = SimpleDateFormat(" dd/M/yyyy")
            val currentDate = sdf.format(Date())
            val text=data.text.toString().trim()+currentDate.toString()
            if (text!="") {
                if (text == "fuck you") {
                    Toast.makeText(this, "No fuck you, leathreman!", Toast.LENGTH_SHORT).show()
                } else {
                    adapter.insert(text,0)
                    data.text.clear()
                }
            }
        }
        }
    }
}