package com.example.auth_reg_with_data

import android.content.Context
import android.net.Uri
import android.util.Patterns
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FirebaseManager(private var context: Context) {
    private var db: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun RegisterUser(context: Context, name: String, email: String, phone: String, password: String, ImgURI: Uri, onSuccess: () -> Unit, onFailure: (String) -> Unit): Boolean {
        try {
            val userRef = db.reference.child("users")
            // Проверка пользователя с именем
            userRef.orderByChild("name").equalTo(name).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            onFailure("Пользователь с таким именем уже существует")
                        }
                        else {
                            // Проверка пользователя с телефоном
                            userRef.orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        if (snapshot.exists()) {
                                            onFailure("Пользователь с таким телефоном уже существует")
                                        }
                                        else {
                                            // Проверка пользователя с email
                                            userRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
                                                    override fun onDataChange(snapshot: DataSnapshot) {
                                                        if (snapshot.exists()) {
                                                            onFailure("Пользователь с таким email уже существует")
                                                        }
                                                        else {
                                                            // Регистрация нового пользователя
                                                            auth.createUserWithEmailAndPassword(email, password)
                                                                .addOnCompleteListener { registrationTask ->
                                                                    // Если регистрация прошла успешно
                                                                    if (registrationTask.isSuccessful) {
                                                                        val dbRef = db.reference.child("users").child(auth.currentUser!!.uid)
                                                                        val users = User(name, email, phone, auth.currentUser!!.uid, ImgURI.toString())
                                                                        dbRef.setValue(users).addOnCompleteListener { databaseTask ->
                                                                                if (databaseTask.isSuccessful) {
                                                                                    onSuccess()
                                                                                }
                                                                                else {
                                                                                    onFailure("Ошибка при создании профиля")
                                                                                }
                                                                        }
                                                                    }
                                                                    else {
                                                                        onFailure("Ошибка при регистрации")
                                                                    }
                                                                }
                                                        }
                                                    }
                                                    override fun onCancelled(error: DatabaseError) {
                                                        onFailure("Ошибка при проверке email")
                                                    }
                                            })
                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        onFailure("Ошибка при проверке номера телефона")
                                    }
                                })
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        onFailure("Ошибка при проверке имени пользователя")
                    }
                })
            return true
        } catch (ex: Exception) {
            onFailure("Ошибка: ${ex.message}")
            return false
        }
    }

    fun AuthUser(context: Context, email: String, password: String, onSuccess: (FirebaseUser) -> Unit, onFailure: (String) -> Unit){
        // авторизация
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    val dbRef = FirebaseDatabase.getInstance().reference.child("users").child(user.uid)
                    dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val userName = snapshot.child("name").value as? String
                            val imgUrl = snapshot.child("imgUrl").value as? String

                            // передача данных в класс хранения
                            DataManager.userEmail=email
                            DataManager.userName=userName
                            DataManager.userImageUrl=imgUrl.toString()
                            onSuccess(user)
                        }
                        override fun onCancelled(error: DatabaseError) {
                            onFailure("Неверные данные или что-то пошло не так")
                        }
                    })
                } else {
                    onFailure("Неверные данные или пользователь отсутсвует")
                }
            } else {
                onFailure("Неверные данные или пользователь отсутствует")
            }
        }
    }

    fun compareEmail(email: EditText){
        if(email.text.toString().isEmpty()){
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()){
            return
        }
        auth.sendPasswordResetEmail(email.text.toString()).addOnCompleteListener {task->
            if (task.isSuccessful){
                Toast.makeText(context,"Проверьте свою почту",Toast.LENGTH_SHORT)
            }
        }
    }

    fun authVK(){

    }
}