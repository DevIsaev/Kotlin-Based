package com.example.auth_reg_with_data

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.auth_reg_with_data.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.Date

class Register : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var storage: FirebaseStorage
    private lateinit var SelectedImg: Uri
    private lateinit var dialog: AlertDialog.Builder
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private var imgUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        dialog = AlertDialog.Builder(this).setMessage("Регистрация...").setCancelable(false)

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
        val PBReg = findViewById<ProgressBar>(R.id.PBReg)

        //изображение
        binding.selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

        BR.setOnClickListener {
            val name = tName.text.toString()
            val email = tEmail.text.toString()
            val phone = tPhone.text.toString()
            val pass1 = tPass.text.toString()
            val pass2 = tConfPass.text.toString()
            PBReg.visibility = View.VISIBLE

            // Проверка на пустоту
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || pass1.isEmpty() || pass2.isEmpty()) {
                when {
                    name.isEmpty() -> tName.error = "Введите имя"
                    email.isEmpty() -> tEmail.error = "Введите email"
                    phone.isEmpty() -> tPhone.error = "Введите номер телефона"
                    pass1.isEmpty() -> tPass.error = "Введите пароль"
                    pass2.isEmpty() -> tConfPass.error = "Подтвердите пароль"
                }
                Toast.makeText(this, "Неверные данные", Toast.LENGTH_SHORT).show()
                PBReg.visibility = View.GONE
            } else if (!email.matches(emailPattern.toRegex())) {
                tEmail.error = "Введите Email корректно"
                Toast.makeText(this, "Введите Email корректно", Toast.LENGTH_SHORT).show()
                PBReg.visibility = View.GONE
            } else if (phone.length != 10) {
                tPhone.error = "Введите номер телефона корректно"
                Toast.makeText(this, "Введите номер телефона корректно", Toast.LENGTH_SHORT).show()
                PBReg.visibility = View.GONE
            } else if (pass1.length < 8) {
                tPass.error = "Введите пароль больше 8 символов"
                Toast.makeText(this, "Введите пароль больше 8 символов", Toast.LENGTH_SHORT).show()
                PBReg.visibility = View.GONE
            } else if (pass2 != pass1) {
                Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                PBReg.visibility = View.GONE
            } else {
                uploadData(name)
                try {
                    // Если все поля не пустые
                    // Внесение данных пользователя в Realtime Database
                    val userRef = db.reference.child("users")
                    // Проверка пользователя с именем
                    userRef.orderByChild("name").equalTo(name)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    PBReg.visibility = View.GONE
                                    Toast.makeText(
                                        this@Register,
                                        "Пользователь с таким именем уже существует",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    // Проверка пользователя с телефоном
                                    userRef.orderByChild("phone").equalTo(phone)
                                        .addListenerForSingleValueEvent(object :
                                            ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if (snapshot.exists()) {
                                                    PBReg.visibility = View.GONE
                                                    Toast.makeText(
                                                        this@Register,
                                                        "Пользователь с таким номером телефона уже существует",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } else {
                                                    // Проверка пользователя с email
                                                    userRef.orderByChild("email").equalTo(email)
                                                        .addListenerForSingleValueEvent(object :
                                                            ValueEventListener {
                                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                                if (snapshot.exists()) {
                                                                    PBReg.visibility = View.GONE
                                                                    Toast.makeText(
                                                                        this@Register,
                                                                        "Пользователь с такой почтой уже существует",
                                                                        Toast.LENGTH_SHORT
                                                                    ).show()
                                                                } else {
                                                                    // Регистрация нового пользователя
                                                                    auth.createUserWithEmailAndPassword(
                                                                        email,
                                                                        pass1
                                                                    )
                                                                        .addOnCompleteListener { registrationTask ->
                                                                            // Если регистрация прошла успешно
                                                                            if (registrationTask.isSuccessful) {
                                                                                val dbRef =
                                                                                    db.reference.child("users")
                                                                                        .child(auth.currentUser!!.uid)
                                                                                val users = User(
                                                                                    name,
                                                                                    email,
                                                                                    phone,
                                                                                    auth.currentUser!!.uid,
                                                                                    imgUrl
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
                                                                                        PBReg.visibility =
                                                                                            View.GONE
                                                                                    }
                                                                            } else {
                                                                                Toast.makeText(
                                                                                    this@Register,
                                                                                    "Неверные данные или что-то пошло не так",
                                                                                    Toast.LENGTH_SHORT
                                                                                ).show()
                                                                                PBReg.visibility = View.GONE
                                                                            }
                                                                        }
                                                                }
                                                            }

                                                            override fun onCancelled(error: DatabaseError) {
                                                                PBReg.visibility = View.GONE
                                                            }
                                                        })
                                                }
                                            }

                                            override fun onCancelled(error: DatabaseError) {
                                                PBReg.visibility = View.GONE
                                            }
                                        })
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                PBReg.visibility = View.GONE
                            }
                        })
                } catch (ex: Exception) {
                    Toast.makeText(this@Register, ex.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun uploadData(uName: String) {
        val referenceImg = storage.reference.child(uName).child(Date().time.toString())
        referenceImg.putFile(SelectedImg).addOnCompleteListener { it ->
            if (it.isSuccessful) {
                referenceImg.downloadUrl.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        imgUrl = task.result.toString()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            if (data.data != null) {
                SelectedImg = data.data!!
                setCircularImage(SelectedImg)
            }
        }
    }

    private fun setCircularImage(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        if (bitmap != null) {
            val circularBitmap = getCircularBitmap(bitmap)
            binding.profileImage.setImageBitmap(circularBitmap)
        }
    }

    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val outputBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(outputBitmap)
        val paint = Paint()
        val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        paint.shader = shader
        paint.isAntiAlias = true

        val radius = bitmap.width.coerceAtMost(bitmap.height) / 2f
        canvas.drawCircle(bitmap.width / 2f, bitmap.height / 2f, radius, paint)

        return outputBitmap
    }
}