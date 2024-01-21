package com.example.musicplayerbasics

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.musicplayerbasics.databinding.ActivitySettingsBinding

class Settings : AppCompatActivity() {
    lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        binding= ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        when(MainActivity.themeIndex){
            0->binding.coolPinkTheme.setBackgroundColor(Color.YELLOW)
            1->binding.coolBlueTheme.setBackgroundColor(Color.YELLOW)
            2->binding.coolPurpleTheme.setBackgroundColor(Color.YELLOW)
            3->binding.coolGreenTheme.setBackgroundColor(Color.YELLOW)
            4->binding.coolBlackTheme.setBackgroundColor(Color.YELLOW)
        }
        binding.coolPinkTheme.setOnClickListener {saveTheme(0)}
        binding.coolBlueTheme.setOnClickListener {saveTheme(1)}
        binding.coolPurpleTheme.setOnClickListener {saveTheme(2)}
        binding.coolGreenTheme.setOnClickListener {saveTheme(3)}
        binding.coolBlackTheme.setOnClickListener {saveTheme(4)}

        binding.versionName.text=setVersion()

        binding.sortBtn.setOnClickListener {
            var menuList= arrayOf("Последнее добавленное", "Название","Размер")
            var currentSort=MainActivity.sort
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Сортировать композиции по:")
                .setPositiveButton("Принять"){_,_ ->
                    var editor=getSharedPreferences("SORTING", MODE_PRIVATE).edit()
                    editor.putInt("sortOrder",currentSort)
                    editor.apply()
                }
                .setSingleChoiceItems(menuList,currentSort){_,which ->
                    currentSort=which
                }

            val customDialog=builder.create()
            customDialog.show()

        }
    }
    private fun saveTheme(index:Int){
        if (MainActivity.themeIndex!=index){

            var editor=getSharedPreferences("THEMES", MODE_PRIVATE).edit()
            editor.putInt("theme",index)
            editor.apply()

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Применить тему")
                .setMessage("Вы действительно хотите применить тему?")
                .setPositiveButton("Да"){_, _ ->
                    exitApp()
                }
                .setNegativeButton("Нет"){dialog,_ -> dialog.dismiss()}
            val customDialog=builder.create()
            customDialog.show()

        }
    }
    private fun setVersion():String{
        //return "Версия: ${BuildConfig.VERSION_NAME}"
        return "Версия: 1.0"
    }
}