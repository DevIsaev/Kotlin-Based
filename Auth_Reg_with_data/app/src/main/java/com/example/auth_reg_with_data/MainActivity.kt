package com.example.auth_reg_with_data

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
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
        binding.tvEmailId.text=DataManager.userEmail
        binding.tvNameId.text=DataManager.userName
        var url=DataManager.userImageUrl.toString()
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
            val builder= AlertDialog.Builder(this)
            builder.setTitle("Выход")
            builder.setMessage("Вы действительно хотите выйти?")
            builder.setPositiveButton("Да",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    auth.signOut()
                    startActivity(Intent(this,Auth::class.java))
                    finish()
            })
            builder.setNegativeButton("Нет",
                DialogInterface.OnClickListener { dialogInterface, i -> })
            builder.show()
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        auth.signOut()
        finish()
    }
}