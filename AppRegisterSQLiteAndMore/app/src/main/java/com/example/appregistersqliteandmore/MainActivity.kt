package com.example.appregistersqliteandmore

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val uLogin=findViewById<EditText>(R.id.loginText)
        val uEmail=findViewById<EditText>(R.id.EmailText)
        val uPass=findViewById<EditText>(R.id.PassText)
        val button=findViewById<Button>(R.id.button)


        button.setOnClickListener {
            val login=uLogin.text.toString().trim()
            val email=uEmail.text.toString().trim()
            val pass=uPass.text.toString().trim()

            if(login==""||email==""||pass==""){
                Toast.makeText(this,"пустые поля",Toast.LENGTH_LONG).show()
            }
            else{
                val u=User(login, email, pass)
                val db=DB(this,null)
                db.RegisterAdd(u)

                Toast.makeText(this,"Пользователь ${login} добавлен",Toast.LENGTH_SHORT).show()

                uLogin.text.clear()
                uEmail.text.clear()
                uPass.text.clear()
            }
        }
    }

}