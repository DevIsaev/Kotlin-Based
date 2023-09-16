package com.example.auth_reg_with_data

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.auth_reg_with_data.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var auth:FirebaseAuth
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth=FirebaseAuth.getInstance()
        if (auth.currentUser==null){
            Toast.makeText(this, "Что то пошло не так, авторизация прошла неудачно", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this,Auth::class.java))
            finish()
            return
        }

        binding.tvUserId.text=intent.getStringExtra("id")
        binding.tvEmailId.text=intent.getStringExtra("email")
        binding.tvNameId.text=intent.getStringExtra("name")
        var url=intent.getStringExtra("image").toString()
        if (!url.isNullOrEmpty()) {
            Toast.makeText(this,"URL не пустой",Toast.LENGTH_SHORT).show()
            Toast.makeText(this,url,Toast.LENGTH_SHORT).show()



            Glide.with(this)
                .load(url)
                .placeholder(R.drawable.grad) // Placeholder image resource
                .error(R.drawable.gradient) // Error image resource
                .into(binding.Avatar)

            }

        else{
            Toast.makeText(this,"URL пустой",Toast.LENGTH_SHORT).show()
        }

        binding.button.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this,Auth::class.java))
            finish()
        }
        
    }

    override fun onDestroy() {
        super.onDestroy()
        auth.signOut()
        finish()
    }
}