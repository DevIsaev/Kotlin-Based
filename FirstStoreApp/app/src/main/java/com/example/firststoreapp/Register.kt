package com.example.firststoreapp

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val linkToAuth=findViewById<TextView>(R.id.bAuth)
        linkToAuth.setOnClickListener {
            val intent= Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        val uLogin=findViewById<EditText>(R.id.loginText)
        val uEmail=findViewById<EditText>(R.id.EmailText)
        val uPass=findViewById<EditText>(R.id.PassText)
        val button=findViewById<Button>(R.id.button)

        setGradient()

        button.setOnClickListener {
            val login=uLogin.text.toString().trim()
            val email=uEmail.text.toString().trim()
            val pass=uPass.text.toString().trim()

            if(login==""||email==""||pass==""){
                Toast.makeText(this,"пустые поля", Toast.LENGTH_LONG).show()
            }
            else{
                val u=User(login, email, pass)
                val db=DB(this,null)
                db.RegisterAdd(u)

                Toast.makeText(this,"Пользователь ${login} добавлен", Toast.LENGTH_SHORT).show()

                uLogin.text.clear()
                uEmail.text.clear()
                uPass.text.clear()
                val intent= Intent(this,MainActivity::class.java)
                startActivity(intent)
            }
        }


    }
    fun setGradient(){
        val myLinearLayout = findViewById<LinearLayout>(R.id.Layout)

        // Определяем цвета градиента (в данном случае, синий и зеленый)
        val colors = intArrayOf(
            getColor(R.color.gradient_start_color),
            getColor(R.color.gradient_end_color)
        )

        // Создаем объект GradientDrawable с градиентом
        val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)

        // Устанавливаем градиентный фон для LinearLayout
        myLinearLayout.background = gradientDrawable
    }
}