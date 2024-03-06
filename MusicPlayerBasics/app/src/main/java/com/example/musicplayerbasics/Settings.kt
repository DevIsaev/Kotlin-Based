package com.example.musicplayerbasics

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import com.example.musicplayerbasics.databinding.ActivitySettingsBinding
import com.example.musicplayerbasics.databinding.SmartColorpickerFromImgBinding

class Settings : AppCompatActivity() {
    lateinit var binding: ActivitySettingsBinding

    private lateinit var bitmap: Bitmap
    private var selectedColor: Int = Color.WHITE
    private val sharedPreferences by lazy {
        getSharedPreferences("CustomTheme", Context.MODE_PRIVATE)
    }

    private val smartColorpickerFromImgBinding: SmartColorpickerFromImgBinding by lazy {
        SmartColorpickerFromImgBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
        binding= ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            val smartDialog= Dialog(this).apply {
                setContentView(smartColorpickerFromImgBinding.root)
                window!!.setLayout(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setCancelable(false)
                smartColorpickerFromImgBinding.colors.isDrawingCacheEnabled=true
                smartColorpickerFromImgBinding.colors.buildDrawingCache(true)
                smartColorpickerFromImgBinding.colors.setOnTouchListener{ v ,event->
                    if (event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE) {
                        val x = event.x.toInt().coerceIn(0, smartColorpickerFromImgBinding.colors.width - 1)
                        val y = event.y.toInt().coerceIn(0, smartColorpickerFromImgBinding.colors.height - 1)

                        bitmap = smartColorpickerFromImgBinding.colors.drawingCache
                        val pixel = bitmap.getPixel(x, y)

                        val r = Color.red(pixel)
                        val g = Color.green(pixel)
                        val b = Color.blue(pixel)

                        selectedColor = Color.rgb(r, g, b)

                        val hex = "#${Integer.toHexString(pixel)}"
                        smartColorpickerFromImgBinding.viewColor.setBackgroundColor(selectedColor)
                        smartColorpickerFromImgBinding.result.text = "RGB: $r,$g,$b\nHEX: $hex"
                    }
                    true
                }
            }

            // Обработчики нажатия на кнопки тем
            binding.coolPinkTheme.setOnClickListener { saveTheme(0) }
            binding.coolBlueTheme.setOnClickListener { saveTheme(1) }
            binding.coolPurpleTheme.setOnClickListener { saveTheme(2) }
            binding.coolGreenTheme.setOnClickListener { saveTheme(3) }
            binding.coolBlackTheme.setOnClickListener { saveTheme(4)}

            binding.coolCustom1Theme.setOnClickListener {
                val b = CheckCustomTheme("coolCustom1Nav")
                if (b) {
                    Toast.makeText(this, "warningcolor", Toast.LENGTH_SHORT).show()
                    smartDialog.show()
                } else {
                    Toast.makeText(this, "no warningcolor", Toast.LENGTH_SHORT).show()
                    saveTheme(5)
                }
            }
            binding.coolCustom2Theme.setOnClickListener {
                val b = CheckCustomTheme("coolCustom2Nav")
                if (b) {
                    Toast.makeText(this, "warningcolor", Toast.LENGTH_SHORT).show()
                    smartDialog.show()
                } else {
                    Toast.makeText(this, "no warningcolor", Toast.LENGTH_SHORT).show()
                    saveTheme(6)
                }
            }
            binding.coolCustom3Theme.setOnClickListener { saveTheme(7) }
            binding.coolCustom4Theme.setOnClickListener { saveTheme(8) }
            binding.coolCustom5Theme.setOnClickListener { saveTheme(9) }

            binding.versionName.text = setVersion()

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

            smartColorpickerFromImgBinding.okBTN.setOnClickListener {
                changeColor("coolCustom2Nav", selectedColor)
                // Сохраняем выбранный цвет
                saveSelectedColor(selectedColor)
                // Закрываем диалог
                smartDialog.dismiss()
                binding.coolCustom1Theme.setBackgroundColor(selectedColor)
                saveTheme(6)
            }

            smartColorpickerFromImgBinding.cancelBTN.setOnClickListener {
                smartDialog.dismiss()
            }
        } catch (ex: Exception) {
            Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun changeColor(style1: String, selectedColor: Int) {
        try {
            val styleId = resources.getIdentifier(style1, "style", packageName)
            val themedContext = ContextThemeWrapper(this, styleId)
            val themeColorsId = themedContext.resources.getIdentifier("themeColors", "attr", packageName)

            if (themeColorsId != 0) {
                val newThemeColorsValue = TypedValue()
                newThemeColorsValue.data = selectedColor
                themedContext.theme.applyStyle(styleId, true)
                themedContext.theme.resolveAttribute(themeColorsId, newThemeColorsValue, true)

                // Update the theme directly in the current activity's window
                theme.applyStyle(styleId, true)
                window.decorView.setBackgroundColor(newThemeColorsValue.data)
            }
        } catch (ex: Exception) {
            Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show()
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
                    // Apply the new theme directly without restarting the app
                    MainActivity.themeIndex = index
                    setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
                    setResult(Activity.RESULT_OK) // Установка результата
                    recreate() // Re-create the activity to reflect the theme change
                    val intent = Intent(this, MainActivity::class.java)
                    startActivityForResult(intent,1)
                    finish() // Закрываем текущую активность, чтобы она не оставалась в стеке
                }
                .setNegativeButton("Нет"){dialog,_ -> dialog.dismiss()}
            val customDialog=builder.create()
            customDialog.show()
        }
    }

    private fun CheckCustomTheme(style:String):Boolean{
        val styleId = resources.getIdentifier(style, "style", packageName)
        val themedContext = ContextThemeWrapper(this, styleId)
        val themeColorsId = themedContext.resources.getIdentifier("themeColors", "attr", packageName)
        if (themeColorsId != 0) {
            val themeColors = TypedValue()
            themedContext.theme.resolveAttribute(themeColorsId, themeColors, true)
            val themeColorsColor = themeColors.data
            if (themeColorsColor == ContextCompat.getColor(themedContext, R.color.warningColor)) {
                return true
            } else {
                return false
            }
        }
        return false
    }

    private fun setVersion():String{
        return "Версия: 1.0"
    }

    // Сохранение выбранного цвета в SharedPreferences
    private fun saveSelectedColor(color: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("selectedColor", color)
        editor.apply()
    }
    private fun loadSelectedColor(): Int {
        return sharedPreferences.getInt("selectedColor", Color.WHITE)
    }
    private fun saveSelectedTheme(index: Int) {
        if (MainActivity.themeIndex != index) {
            var editor = getSharedPreferences("THEMES", MODE_PRIVATE).edit()
            editor.putInt("theme", index)
            editor.apply()

            // Применяем новую тему сразу после сохранения
            MainActivity.themeIndex = index
            setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
            recreate() // Пересоздаем Activity, чтобы применить новую тему
            setResult(Activity.RESULT_OK)
        }
    }

    override fun onBackPressed() {
        // Set the result to indicate that the theme has not been changed
        setResult(Activity.RESULT_CANCELED)
        super.onBackPressed()
    }
}