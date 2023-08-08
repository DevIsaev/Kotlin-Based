package com.example.firebaseauth_reg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button=findViewById<Button>(R.id.button)
        button.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent= Intent(this,AuthActivity::class.java)
            startActivity(intent)
            finish()
        }
        val uUser=findViewById<TextView>(R.id.tvUserId)
        val EmailId=findViewById<TextView>(R.id.tvEmailId)

        uUser.text=intent.getStringExtra("id")
        EmailId.text=intent.getStringExtra("email")

    }
}