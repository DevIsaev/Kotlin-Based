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


class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val linkToAuth=findViewById<TextView>(R.id.bAuth)
        linkToAuth.setOnClickListener {
            onBackPressed()
        }

        //val uLogin=findViewById<EditText>(R.id.loginText)
        val uEmail=findViewById<EditText>(R.id.EmailText)
        val uPass=findViewById<EditText>(R.id.PassText)
        val button=findViewById<Button>(R.id.button)

        button.setOnClickListener {
            when {
                TextUtils.isEmpty(uEmail.text.toString().trim() { it <= ' ' }) -> {
                    Toast.makeText(this@RegisterActivity, "Please enter email", Toast.LENGTH_SHORT).show()
                }

                TextUtils.isEmpty(uPass.text.toString().trim() { it <= ' ' }) -> {
                    Toast.makeText(this@RegisterActivity, "Please enter password", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    var email:String=uEmail.text.toString()
                    var password:String=uEmail.text.toString()

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(
                            OnCompleteListener<AuthResult> {task ->
                                if(task.isSuccessful){
                                    var FBUser:FirebaseUser=task.result!!.user!!
                                    Toast.makeText(this@RegisterActivity, "Successfully Register", Toast.LENGTH_SHORT).show()

                                    val intent= Intent(this@RegisterActivity,MainActivity::class.java)
                                    intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    intent.putExtra("id",FBUser.uid)
                                    intent.putExtra("email",email)
                                    startActivity(intent)
                                    finish()
                                }
                                else{
                                    Toast.makeText(this@RegisterActivity, task.exception!!.message.toString(), Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                }
            }
        }
    }
}