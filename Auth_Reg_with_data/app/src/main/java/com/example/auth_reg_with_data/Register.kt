package com.example.auth_reg_with_data

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Register : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    var SavedIamge:String?=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val bReg = findViewById<TextView>(R.id.bAuth)
        bReg.setOnClickListener {
            onBackPressed()
        }

        val tName = findViewById<EditText>(R.id.NameText)
        val tEmail = findViewById<EditText>(R.id.EmailText)
        val tPhone = findViewById<EditText>(R.id.PhoneText)
        val tPass = findViewById<EditText>(R.id.PassText)
        val tConfPass = findViewById<EditText>(R.id.ConfPassText)
        val BR = findViewById<Button>(R.id.button)
        val PB = findViewById<ProgressBar>(R.id.PBReg)



        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()


        val profileImage = findViewById<ImageView>(R.id.profileImage)
        val selectImageButton = findViewById<Button>(R.id.selectImageButton)

        selectImageButton.setOnClickListener {

        }






        BR.setOnClickListener {
            val name = tName.text.toString()
            val email = tEmail.text.toString()
            val phone = tPhone.text.toString()
            val pass1 = tPass.text.toString()
            val pass2 = tConfPass.text.toString()
            PB.visibility = View.VISIBLE



            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || pass1.isEmpty() || pass2.isEmpty()) {
                when {
                    name.isEmpty() -> tName.error = "Введите имя"
                    email.isEmpty() -> tEmail.error = "Введите email"
                    phone.isEmpty() -> tPhone.error = "Введите номер телефона"
                    pass1.isEmpty() -> tPass.error = "Введите пароль"
                    pass2.isEmpty() -> tConfPass.error = "Подтвердите пароль"
                }
                Toast.makeText(this, "Неверные данные", Toast.LENGTH_SHORT).show()
                PB.visibility = View.GONE

            } else if (!email.matches(emailPattern.toRegex())) {
                tEmail.error = "Введите Email корректно"
                Toast.makeText(this, "Введите Email корректно", Toast.LENGTH_SHORT).show()
                PB.visibility = View.GONE
            } else if (phone.length != 10) {
                tPhone.error = "Введите номер телефона корректно"
                Toast.makeText(this, "Введите номер телефона корректно", Toast.LENGTH_SHORT).show()
                PB.visibility = View.GONE
            } else if (pass1.length < 8) {
                tPass.error = "Введите пароль больше 8 символов"
                Toast.makeText(this, "Введите пароль больше 8 символов", Toast.LENGTH_SHORT).show()
                PB.visibility = View.GONE

            } else if (pass2 != pass1) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                PB.visibility = View.GONE
            } else {

                val userRef = db.reference.child("users")
                userRef.orderByChild("name").equalTo(name)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                PB.visibility = View.GONE
                                Toast.makeText(this@Register, "Пользователь с таким именем уже существует", Toast.LENGTH_SHORT).show()
                            } else {

                                userRef.orderByChild("phone").equalTo(phone)
                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.exists()) {
                                                PB.visibility = View.GONE
                                                Toast.makeText(this@Register, "Пользователь с таким номером телефона уже существует", Toast.LENGTH_SHORT).show()
                                            } else {

                                                userRef.orderByChild("email").equalTo(email)
                                                    .addListenerForSingleValueEvent(object : ValueEventListener {
                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                            if (snapshot.exists()) {
                                                                PB.visibility = View.GONE
                                                                Toast.makeText(this@Register, "Пользователь с такой почтой уже существует", Toast.LENGTH_SHORT).show()
                                                            } else {

                                                                // Регистрация нового пользователя
                                                                auth.createUserWithEmailAndPassword(
                                                                    email,
                                                                    pass1
                                                                )
                                                                    .addOnCompleteListener { registrationTask ->
                                                                        if (registrationTask.isSuccessful) {
                                                                            val dbRef =
                                                                                db.reference.child("users")
                                                                                    .child(auth.currentUser!!.uid)
                                                                            val users = User(
                                                                                name,
                                                                                email,
                                                                                phone,
                                                                                auth.currentUser!!.uid
                                                                            )
                                                                            dbRef.setValue(users)
                                                                                .addOnCompleteListener { databaseTask ->
                                                                                    if (databaseTask.isSuccessful) {
                                                                                        Toast.makeText(
                                                                                            this@Register,
                                                                                            "Регистрация прошла успешно!",
                                                                                            Toast.LENGTH_SHORT
                                                                                        ).show()
                                                                                        val intent =
                                                                                            Intent(
                                                                                                this@Register,
                                                                                                Auth::class.java
                                                                                            )
                                                                                        startActivity(
                                                                                            intent
                                                                                        )
                                                                                    } else {
                                                                                        Toast.makeText(
                                                                                            this@Register,
                                                                                            "Неверные данные или что-то пошло не так",
                                                                                            Toast.LENGTH_SHORT
                                                                                        ).show()
                                                                                    }
                                                                                    PB.visibility =
                                                                                        View.GONE
                                                                                }
                                                                        } else {
                                                                            Toast.makeText(
                                                                                this@Register,
                                                                                "Неверные данные или что-то пошло не так",
                                                                                Toast.LENGTH_SHORT
                                                                            ).show()
                                                                            PB.visibility =
                                                                                View.GONE
                                                                        }
                                                                    }
                                                            }
                                                        }

                                                        override fun onCancelled(error: DatabaseError) {
                                                            PB.visibility = View.GONE
                                                        }
                                                    })
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            PB.visibility = View.GONE
                                        }
                                    })
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            PB.visibility = View.GONE
                        }
                    })
            }
        }
    }

}