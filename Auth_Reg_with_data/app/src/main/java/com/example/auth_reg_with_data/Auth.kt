package com.example.auth_reg_with_data

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.auth_reg_with_data.databinding.ActivityAuthBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Auth : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
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
            // проверка на пустоту
            if (email.isEmpty() || pass.isEmpty()) {
                when {
                    email.isEmpty() -> binding1.EmailAuth.error = "Введите email"
                    pass.isEmpty() -> binding1.PassAuth.error = "Введите пароль"
                }
                Toast.makeText(this, "Неверные данные", Toast.LENGTH_SHORT).show()
                binding1.PBAuth.visibility = View.GONE
            } else if (!email.matches(emailPattern.toRegex())) {
                binding1.EmailAuth.error = "Введите Email корректно"
                Toast.makeText(this, "Введите Email корректно", Toast.LENGTH_SHORT).show()
                binding1.PBAuth.visibility = View.GONE
            } else if (pass.length < 8) {
                binding1.PassAuth.error = "Введите пароль корректно"
                Toast.makeText(this, "Введите пароль корректно", Toast.LENGTH_SHORT).show()
                binding1.PBAuth.visibility = View.GONE
            } else {
                authFun(email, pass)
            }
        }
    }

    private fun authAsGuset() {
        val intent = Intent(this@Auth, ProfileActivity::class.java)
        intent.putExtra("email", "Не авторизован")
        intent.putExtra("name", "Не авторизован")
        startActivity(intent)
    }

    fun authFun(email:String,pass:String){
        auth = FirebaseAuth.getInstance()
        db=FirebaseDatabase.getInstance()
        // авторизация
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    val dbRef = FirebaseDatabase.getInstance().reference.child("users").child(user.uid)
                    dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userName = snapshot.child("name").value as? String
                            val imgUrl = snapshot.child("imgUrl").value as? String

                            // передача данных в активити
                            val intent = Intent(this@Auth, ProfileActivity::class.java)
                            //intent.putExtra("id", user.uid)
                            intent.putExtra("email", email)
                            intent.putExtra("name", userName)
                            intent.putExtra("image@#", imgUrl)
                            //Toast.makeText(this@Auth,imgUrl.toString(),Toast.LENGTH_LONG).show()
                            startActivity(intent)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Обработка ошибок получения данных
                            binding1.PBAuth.visibility = View.GONE
                        }
                    })
                } else {
                    Toast.makeText(this, "Неверные данные или что-то пошло не так", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Неверные данные или пользователь отстутствует", Toast.LENGTH_SHORT).show()
            }
            binding1.PBAuth.visibility = View.GONE
        }
    }
}