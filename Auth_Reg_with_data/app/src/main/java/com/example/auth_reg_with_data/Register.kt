package com.example.auth_reg_with_data

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.auth_reg_with_data.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.profileImage
import java.util.Date

class Register : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var storage: FirebaseStorage
    private lateinit var SelectedImg: Uri
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private var imgUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        binding.PBReg.setOnClickListener {
            onBackPressed()
        }
        //изображение
        binding.selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }
        binding.button.setOnClickListener {
            val name = binding.NameText.text.toString()
            val email = binding.EmailText.text.toString()
            val phone = binding.PhoneText.text.toString()
            val pass1 = binding.PassText.text.toString()
            val pass2 = binding.ConfPassText.text.toString()
            binding.PBReg.visibility = View.VISIBLE
            PaternsAndEmpty(name,email,phone,pass1,pass2)
        }
    }

    private fun PaternsAndEmpty(name:String,email:String,phone:String,pass1:String,pass2:String){
        // Проверка на пустоту
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || pass1.isEmpty() || pass2.isEmpty()) {
            when {
                name.isEmpty() ->  binding.NameText.error = "Введите имя"
                email.isEmpty() -> binding.EmailText.error = "Введите email"
                phone.isEmpty() -> binding.PhoneText.error = "Введите номер телефона"
                pass1.isEmpty() -> binding.PassText.error = "Введите пароль"
                pass2.isEmpty() -> binding.ConfPassText.error = "Подтвердите пароль"
            }
            Toast.makeText(this, "Неверные данные", Toast.LENGTH_SHORT).show()
            binding.PBReg.visibility = View.GONE
        } else if (!email.matches(emailPattern.toRegex())) {
            binding.EmailText.error = "Введите Email корректно"
            Toast.makeText(this, "Введите Email корректно", Toast.LENGTH_SHORT).show()
            binding.PBReg.visibility = View.GONE
        } else if (phone.length != 10) {
            binding.PhoneText.error = "Введите номер телефона корректно"
            Toast.makeText(this, "Введите номер телефона корректно", Toast.LENGTH_SHORT).show()
            binding.PBReg.visibility = View.GONE
        } else if (pass1.length < 8) {
            binding.PassText.error = "Введите пароль больше 8 символов"
            Toast.makeText(this, "Введите пароль больше 8 символов", Toast.LENGTH_SHORT).show()
            binding.PBReg.visibility = View.GONE
        } else if (pass2 != pass1) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
            binding.PBReg.visibility = View.GONE
        }
            else if (SelectedImg==null||profileImage.drawable==null) {
                Toast.makeText(this, "Выберите изображение для аватара", Toast.LENGTH_SHORT).show()
                binding.PBReg.visibility = View.GONE
            }
        else {
            uploadImg(name)
            uploadUser(name,email,phone,pass1)
        }
    }

    private fun uploadUser(name: String, email: String, phone: String, pass1: String) {
        try {
            // Если все поля не пустые
            // Внесение данных пользователя в Realtime Database
            val userRef = db.reference.child("users")

            // Проверка пользователя с именем
            userRef.orderByChild("name").equalTo(name)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            binding.PBReg.visibility = View.GONE
                            Toast.makeText(this@Register, "Пользователь с таким именем уже существует", Toast.LENGTH_SHORT).show()
                        } else {

                            // Проверка пользователя с телефоном
                            userRef.orderByChild("phone").equalTo(phone)
                                .addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            binding.PBReg.visibility = View.GONE
                                            Toast.makeText(this@Register, "Пользователь с таким номером телефона уже существует", Toast.LENGTH_SHORT).show()
                                        } else {

                                            // Проверка пользователя с email
                                            userRef.orderByChild("email").equalTo(email)
                                                .addListenerForSingleValueEvent(object :
                                                    ValueEventListener {
                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                        if (snapshot.exists()) {
                                                            binding.PBReg.visibility = View.GONE
                                                            Toast.makeText(this@Register, "Пользователь с такой почтой уже существует", Toast.LENGTH_SHORT).show()
                                                        } else {

                                                            // Регистрация нового пользователя
                                                            auth.createUserWithEmailAndPassword(email, pass1)
                                                                .addOnCompleteListener { registrationTask ->
                                                                    // Если регистрация прошла успешно
                                                                    if (registrationTask.isSuccessful) {
                                                                        val dbRef =
                                                                            db.reference.child("users").child(auth.currentUser!!.uid)
                                                                        val users = User(name, email, phone, auth.currentUser!!.uid, imgUrl)
                                                                        dbRef.setValue(users)
                                                                            .addOnCompleteListener { databaseTask ->
                                                                                if (databaseTask.isSuccessful) {
                                                                                    Toast.makeText(this@Register, "Регистрация прошла успешно!", Toast.LENGTH_SHORT).show()
                                                                                    val intent = Intent(
                                                                                        this@Register, Auth::class.java)
                                                                                    startActivity(intent)
                                                                                } else {
                                                                                    Toast.makeText(this@Register, "Неверные данные или что-то пошло не так", Toast.LENGTH_SHORT).show()
                                                                                }
                                                                                binding.PBReg.visibility = View.GONE
                                                                            }
                                                                    } else {
                                                                        Toast.makeText(this@Register, "Неверные данные или что-то пошло не так", Toast.LENGTH_SHORT).show()
                                                                        binding.PBReg.visibility = View.GONE
                                                                    }
                                                                }
                                                        }
                                                    }

                                                    override fun onCancelled(error: DatabaseError) {
                                                        binding.PBReg.visibility = View.GONE
                                                    }
                                                })
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        binding.PBReg.visibility = View.GONE
                                    }
                                })
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        binding.PBReg.visibility = View.GONE
                    }
                })
        } catch (ex: Exception) {
            Toast.makeText(this@Register, ex.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadImg(uName: String) {
        val referenceImg = storage.reference.child("UsersProfiles").child(uName).child(Date().time.toString())
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
        if (requestCode == 1 && resultCode == RESULT_OK) {
            data?.data?.let { SelectedImg = it }
            // Здесь вы также можете установить выбранное изображение в ваш ImageView
            binding.profileImage.setImageURI(SelectedImg)
        }

//        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
//            if (data.data != null) {
//                SelectedImg = data.data!!
//                setCircularImage(SelectedImg)
//            }
//        }
    }

}