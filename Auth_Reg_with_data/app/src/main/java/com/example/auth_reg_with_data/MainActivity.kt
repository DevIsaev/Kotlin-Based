package com.example.auth_reg_with_data

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.RequestOptions
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


        //binding.tvUserId.text=intent.getStringExtra("id")
        binding.tvEmailId.text=intent.getStringExtra("email")
        binding.tvNameId.text=intent.getStringExtra("name")
        var url=intent.getStringExtra("image@#").toString()
        if (!url.isNullOrEmpty()) {
            val options = RequestOptions().format(DecodeFormat.PREFER_ARGB_8888)
                 Glide.with(this)
                .asDrawable()
                .error(R.drawable.avatar)
                .apply(options)
                .load(url)
                .into(binding.Avatar)
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