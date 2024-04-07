package com.example.musicplayerbasics

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate

class SharedPreferenceManager(context:Context) {
    private val  preference=context.getSharedPreferences(context.packageName,AppCompatActivity.MODE_PRIVATE)
    private var editor=preference.edit()
    private val key="systheme"
    var theme
        get()=preference.getInt(key,2)
        set(value) {
            editor.putInt(key,value)
            editor.commit()
        }
    var themeFlag= arrayOf(AppCompatDelegate.MODE_NIGHT_NO,
        AppCompatDelegate.MODE_NIGHT_YES,
        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
}