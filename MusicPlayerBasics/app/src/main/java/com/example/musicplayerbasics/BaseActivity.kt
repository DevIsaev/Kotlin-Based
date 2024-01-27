package com.example.musicplayerbasics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppTheme()
    }

    private fun setAppTheme() {
        val themeIndex = getThemeIndexFromPreferences()
        setTheme(MainActivity.currentThemeNav[themeIndex])
    }

    private fun getThemeIndexFromPreferences(): Int {
        val sharedPreferences = getSharedPreferences("THEMES", MODE_PRIVATE)
        return sharedPreferences.getInt("theme", 0)
    }
}