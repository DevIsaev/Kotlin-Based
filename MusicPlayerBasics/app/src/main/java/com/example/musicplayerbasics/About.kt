package com.example.musicplayerbasics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayerbasics.databinding.ActivityAboutBinding

class About : AppCompatActivity() {
    lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        binding= ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.aboutText.text=aboutText()


    }
    private fun aboutText():String{
        return "Это прототип аудиоплеера, разработанный по гайдам \n" +
                "индуса Harsh H. Rajpurohit, для дальнейшей \n" +
                "самостоятельной разработки собственного плеера"
    }
}