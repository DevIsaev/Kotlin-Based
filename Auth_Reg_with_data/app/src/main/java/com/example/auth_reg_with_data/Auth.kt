package com.example.auth_reg_with_data

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Auth : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        var bReg = findViewById<TextView>(R.id.bReg)
        bReg.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        val tEmail = findViewById<EditText>(R.id.EmailAuth)
        val tPass = findViewById<EditText>(R.id.PassAuth)
        val BAuth = findViewById<Button>(R.id.buttonAuth)
        val PB = findViewById<ProgressBar>(R.id.PBAuth)

        auth = FirebaseAuth.getInstance()
        db=FirebaseDatabase.getInstance()

        BAuth.setOnClickListener {
            val email = tEmail.text.toString()
            val pass = tPass.text.toString()
            PB.visibility = View.VISIBLE

            if (email.isEmpty() || pass.isEmpty()) {
                when {
                    email.isEmpty() -> tEmail.error = "Введите email"
                    pass.isEmpty() -> tPass.error = "Введите пароль"
                }
                Toast.makeText(this, "Неверные данные", Toast.LENGTH_SHORT).show()
                PB.visibility = View.GONE
            } else if (!email.matches(emailPattern.toRegex())) {
                tEmail.error = "Введите Email корректно"
                Toast.makeText(this, "Введите Email корректно", Toast.LENGTH_SHORT).show()
                PB.visibility = View.GONE
            } else if (pass.length < 8) {
                tPass.error = "Введите пароль больше 8 символов"
                Toast.makeText(this, "Введите пароль больше 8 символов", Toast.LENGTH_SHORT).show()
                PB.visibility = View.GONE
            } else {
                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        if (user != null) {
                            val dbRef = FirebaseDatabase.getInstance().reference.child("users").child(user.uid)
                            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val userName = snapshot.child("name").value as? String

                                    // Передаем имя пользователя в MainActivity
                                    val intent = Intent(this@Auth, MainActivity::class.java)
                                    intent.putExtra("id", user.uid)
                                    intent.putExtra("email", email)
                                    intent.putExtra("name", userName)
                                    startActivity(intent)
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    // Обработка ошибок получения данных
                                    PB.visibility = View.GONE
                                }
                            })
                        } else {
                            Toast.makeText(
                                this,
                                "Неверные данные или что-то пошло не так",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            this,
                            "Неверные данные или что-то пошло не так",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    PB.visibility = View.GONE
                }
            }
        }
    }
}
