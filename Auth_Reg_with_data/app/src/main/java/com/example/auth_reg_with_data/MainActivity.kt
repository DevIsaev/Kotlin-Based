package com.example.auth_reg_with_data

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var Auth:FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*Auth=FirebaseAuth.getInstance()
        if (Auth.currentUser==null){
            val intent=Intent(this,Auth::class.java)
            startActivity(intent)
        }*/

        val button=findViewById<Button>(R.id.button)
        button.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent= Intent(this,Auth::class.java)
            startActivity(intent)
            finish()
        }
        val uUser=findViewById<TextView>(R.id.tvUserId)
        val EmailId=findViewById<TextView>(R.id.tvEmailId)
        val name=findViewById<TextView>(R.id.tvNameId)
        val profileImage = findViewById<ImageView>(R.id.Avatar)

        uUser.text=intent.getStringExtra("id")
        EmailId.text=intent.getStringExtra("email")
        name.text=intent.getStringExtra("name")

    }
}