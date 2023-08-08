package com.example.firebaseauth_reg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        val linkToReg = findViewById<TextView>(R.id.bReg)
        linkToReg.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        val uEmail = findViewById<EditText>(R.id.loginTextAuth)
        val uPass = findViewById<EditText>(R.id.PassTextAuth)
        val button = findViewById<Button>(R.id.buttonAuth)

        button.setOnClickListener {
            when {
                TextUtils.isEmpty(uEmail.text.toString().trim() { it <= ' ' }) -> {
                    Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show()
                }

                TextUtils.isEmpty(uPass.text.toString().trim() { it <= ' ' }) -> {
                    Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    var email:String=uEmail.text.toString()
                    var password:String=uEmail.text.toString()

                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener{task ->
                                if(task.isSuccessful){
                                    Toast.makeText(this, "Successfully Login", Toast.LENGTH_SHORT).show()

                                    val intent= Intent(this,MainActivity::class.java)
                                    intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.putExtra("id",FirebaseAuth.getInstance().currentUser!!.uid)
                                    intent.putExtra("email",email)
                                    startActivity(intent)
                                    finish()
                                }
                                else{
                                    Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                                }
                        }
                }
            }
        }
    }
}