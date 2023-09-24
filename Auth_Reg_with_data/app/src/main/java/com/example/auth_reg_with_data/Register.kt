package com.example.auth_reg_with_data

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.auth_reg_with_data.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Register : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseDatabase
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var storage: FirebaseStorage
    //private lateinit var SelectedImg: Uri
    //private var imgUrl: String? = null
    private var ImgURI: Uri?=null
    private var emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}"
    //private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    private var phoneNumberPattern = "^(\\+7|8)[0-9]{10}$"
    private var nicknamePattern = "^[a-zA-Z0-9_]*$"
    private var passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}"


    private lateinit var storageRef:StorageReference
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        firebaseFirestore=FirebaseFirestore.getInstance()


        binding.PBReg.setOnClickListener {
            onBackPressed()
        }
        val resultLauncher=registerForActivityResult(ActivityResultContracts.GetContent()){
            // Проверяем, что выбранный файл - изображение
            if (isValidImage(it)) {
                binding.profileImage.setImageURI(it)
                ImgURI = it
            } else {
                Toast.makeText(this@Register, "GIF изображения пока не поддерживаются", Toast.LENGTH_SHORT).show()
            }
        }

        //изображение
        binding.selectImageButton.setOnClickListener {
              resultLauncher.launch("image/*")
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
        }
        else if (!name.matches(nicknamePattern.toRegex())) {
            binding.NameText.error = "Введите Никнейм корректно"
            Toast.makeText(this, " Никнейм может содержать только буквы, цифры и символы подчеркивания", Toast.LENGTH_SHORT).show()
            binding.PBReg.visibility = View.GONE
        }
        else if (!email.matches(emailPattern.toRegex())) {
            binding.EmailText.error = "Введите Email корректно"
            Toast.makeText(this, "Введите Email корректно", Toast.LENGTH_SHORT).show()
            binding.PBReg.visibility = View.GONE
        } else if (!phone.matches(phoneNumberPattern.toRegex())) {
            binding.PhoneText.error = "Введите номер телефона корректно"
            Toast.makeText(this, "Введите номер телефона корректно", Toast.LENGTH_SHORT).show()
            binding.PBReg.visibility = View.GONE
        } else if (pass1.length<8) {
            binding.PassText.error = "Введите пароль больше 8 символов"
            Toast.makeText(this, "Пароль: минимум 8 символов с буквами, цифрами и специальными символами", Toast.LENGTH_SHORT).show()
            binding.PBReg.visibility = View.GONE
        } else if (pass2 != pass1) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
            binding.PBReg.visibility = View.GONE
        }
        else if (ImgURI==null) {
            Toast.makeText(this, "Выберите изображение для аватара", Toast.LENGTH_SHORT).show()
            binding.PBReg.visibility = View.GONE
        }
        else {
            uploadImage(name)
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
                                                                        val dbRef = db.reference.child("users").child(auth.currentUser!!.uid)
                                                                        val users = User(name, email, phone, auth.currentUser!!.uid, ImgURI.toString())
                                                                        dbRef.setValue(users).addOnCompleteListener { databaseTask ->
                                                                                if (databaseTask.isSuccessful) {
                                                                                    Toast.makeText(this@Register, "Регистрация прошла успешно!", Toast.LENGTH_SHORT).show()
                                                                                    val intent = Intent(this@Register, Auth::class.java)
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

    private fun uploadImage(uName: String){
        try {
            storageRef = FirebaseStorage.getInstance().reference.child("UsersProfiles").child(uName).child(1.toString())

            ImgURI?.let {
                storageRef.putFile(it).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
//                            val map = HashMap<String, Any>()
//                            map["pic"] = uri.toString()
                            val map= mapOf("url" to it.toString())
                            firebaseFirestore.collection(uName).add(map)
                                .addOnCompleteListener { firestoreTask ->
                                    if (firestoreTask.isSuccessful) {
                                        Toast.makeText(this, "Great", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(this, "Что то не так с изображением", Toast.LENGTH_SHORT).show()
                                        binding.PBReg.visibility = View.GONE
                                    }
                                }
                        }
                    } else {
                        Toast.makeText(this, "0", Toast.LENGTH_SHORT).show()
                        binding.PBReg.visibility = View.GONE
                    }
                }
            }
        }
        catch (ex:Exception){
            Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    fun isValidImage(uri: Uri?): Boolean {
        uri?.let {
            val contentResolver = applicationContext.contentResolver
            val type = contentResolver.getType(it)
            return type?.startsWith("image/") == true && !type.endsWith("gif")
        }
        return false
    }
}