package com.example.musicplayerbasics

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class myApp:Application() {
    override fun onCreate() {
        super.onCreate()
        var spm=SharedPreferenceManager(this)
        AppCompatDelegate.setDefaultNightMode(spm.themeFlag[spm.theme])
    }
}