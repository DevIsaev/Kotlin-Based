package com.example.auth_reg_with_data

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.auth_reg_with_data.databinding.ActivityAuthBinding

class Auth : AppCompatActivity() {
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private lateinit var binding1: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        binding1 = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding1.root)

        binding1.bReg.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
        binding1.bGo.setOnClickListener {
            authAsGuset()
        }

        binding1.buttonAuth.setOnClickListener {
            val email = binding1.EmailAuth.text.toString()
            val pass = binding1.PassAuth.text.toString()
            binding1.PBAuth.visibility = View.VISIBLE
            CheckedAndPattern(email,pass)
        }
    }

    private fun CheckedAndPattern(email:String,pass:String){
        // проверка на пустоту
        if (email.isEmpty() || pass.isEmpty()) {
            when {
                email.isEmpty() -> binding1.EmailAuth.error = "Введите email"
                pass.isEmpty() -> binding1.PassAuth.error = "Введите пароль"
            }
            Toast.makeText(this, "Неверные данные", Toast.LENGTH_SHORT).show()
            binding1.PBAuth.visibility = View.GONE
        }
        else if (!email.matches(emailPattern.toRegex())) {
            binding1.EmailAuth.error = "Введите Email корректно"
            Toast.makeText(this, "Введите Email корректно", Toast.LENGTH_SHORT).show()
            binding1.PBAuth.visibility = View.GONE
        }
        else if (pass.length < 8) {
            binding1.PassAuth.error = "Введите пароль корректно"
            Toast.makeText(this, "Введите пароль корректно", Toast.LENGTH_SHORT).show()
            binding1.PBAuth.visibility = View.GONE
        }
        else {
            val fbm=FirebaseManager(this)
            fbm.AuthUser(email,pass,{
                Toast.makeText(this, "Вы вошли успешно", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@Auth, ProfileActivity::class.java)
                startActivity(intent)
                binding1.PBAuth.visibility = View.GONE
            }, {
                Toast.makeText(this, "Что то пошло не так", Toast.LENGTH_SHORT).show()
                binding1.PBAuth.visibility = View.GONE
            })
        }
    }

    private fun authAsGuset() {
        DataManager.userEmail
        DataManager.userName
        val intent = Intent(this@Auth, ProfileActivity::class.java)
        startActivity(intent)
    }
}