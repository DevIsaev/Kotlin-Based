package com.example.firststoreapp

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Debug
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setGradient()
        val linkToReg=findViewById<TextView>(R.id.bReg)
        linkToReg.setOnClickListener {
            val intent=Intent(this,Register::class.java)
            startActivity(intent)
        }

        val uLogin=findViewById<EditText>(R.id.loginTextAuth)
        val uPass=findViewById<EditText>(R.id.PassTextAuth)
        val button=findViewById<Button>(R.id.buttonAuth)


        button.setOnClickListener {
            val login=uLogin.text.toString().trim()
            val pass=uPass.text.toString().trim()

            if(login==""||pass==""){
                Toast.makeText(this,"пустые поля", Toast.LENGTH_LONG).show()
            }
            else{
                val db=DB(this,null)
                val isAuth=db.AuthGet(login,pass)

                if (isAuth) {
                    try {


                        Toast.makeText(this, "Пользователь ${login} вошел", Toast.LENGTH_SHORT)
                            .show()
                        uLogin.text.clear()
                        uPass.text.clear()
                        val intent = Intent(this, ItemsActivity::class.java)
                        startActivity(intent)

                    }
                    catch (ex:Exception){
                            Toast.makeText(this,ex.toString(),Toast.LENGTH_SHORT).show()
                    }
                }

                else{
                    Toast.makeText(this, "Пользователь ${login} не существует или неверные данные", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }
    fun setGradient(){
        val myLinearLayout = findViewById<LinearLayout>(R.id.Layout)

        // Определяем цвета градиента (в данном случае, синий и зеленый)
        val colors = intArrayOf(
            getColor(R.color.gradient_start_color1),
            getColor(R.color.gradient_end_color1)
        )

        // Создаем объект GradientDrawable с градиентом
        val gradientDrawable = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)

        // Устанавливаем градиентный фон для LinearLayout
        myLinearLayout.background = gradientDrawable
    }
}