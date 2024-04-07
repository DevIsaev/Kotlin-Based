package com.example.musicplayerbasics

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.musicplayerbasics.databinding.FragmentSettingsBinding
import com.example.musicplayerbasics.databinding.SmartColorpickerFromImgBinding

class SettingsFragment : Fragment() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentSettingsBinding

    }

    private lateinit var bitmap: Bitmap
    private var selectedColor: Int = Color.WHITE
    private val sharedPreferences by lazy {
        requireContext().getSharedPreferences("CustomTheme", MODE_PRIVATE)
    }

    private val smartColorpickerFromImgBinding: SmartColorpickerFromImgBinding by lazy {
        SmartColorpickerFromImgBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            binding.lightBTN.setOnClickListener {
                sysTheme(0)
                //themeDialog.show()
            }
            binding.nightBTN.setOnClickListener {
                sysTheme(1)
                //themeDialog.show()
            }
            binding.autoBTN.setOnClickListener {
                sysTheme(2)
                //themeDialog.show()
            }
            val smartDialog = Dialog(requireContext()).apply {
                setContentView(smartColorpickerFromImgBinding.root)
                window!!.setLayout(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setCancelable(false)
                smartColorpickerFromImgBinding.colors.isDrawingCacheEnabled = true
                smartColorpickerFromImgBinding.colors.buildDrawingCache(true)
                smartColorpickerFromImgBinding.colors.setOnTouchListener { v, event ->
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
            binding.coolBlackTheme.setOnClickListener { saveTheme(4) }

            binding.coolCustom1Theme.setOnClickListener {
                val b = CheckCustomTheme("coolCustom1Nav")
                if (b) {
                    Toast.makeText(requireContext(), "warningcolor", Toast.LENGTH_SHORT).show()
                    smartDialog.show()
                } else {
                    Toast.makeText(requireContext(), "no warningcolor", Toast.LENGTH_SHORT).show()
                    saveTheme(5)
                }
            }
            binding.coolCustom2Theme.setOnClickListener {
                val b = CheckCustomTheme("coolCustom2Nav")
                if (b) {
                    Toast.makeText(requireContext(), "warningcolor", Toast.LENGTH_SHORT).show()
                    smartDialog.show()
                } else {
                    Toast.makeText(requireContext(), "no warningcolor", Toast.LENGTH_SHORT).show()
                    saveTheme(6)
                }
            }
            binding.coolCustom3Theme.setOnClickListener {
                saveTheme(7)
            }
            binding.coolCustom4Theme.setOnClickListener {
                saveTheme(8)
            }
            binding.coolCustom5Theme.setOnClickListener {
                saveTheme(9)
            }

            when(MainActivity.themeIndex){
                0 -> binding.coolPinkTheme.setBackgroundColor(Color.YELLOW)
                1 -> binding.coolBlueTheme.setBackgroundColor(Color.YELLOW)
                2 -> binding.coolPurpleTheme.setBackgroundColor(Color.YELLOW)
                3 -> binding.coolGreenTheme.setBackgroundColor(Color.YELLOW)
                4 -> binding.coolBlackTheme.setBackgroundColor(Color.YELLOW)
                5->binding.coolCustom1Theme.setBackgroundColor(Color.YELLOW)
                6->binding.coolCustom2Theme.setBackgroundColor(Color.YELLOW)
                7->binding.coolCustom3Theme.setBackgroundColor(Color.YELLOW)
                8->binding.coolCustom4Theme.setBackgroundColor(Color.YELLOW)
                9->binding.coolCustom5Theme.setBackgroundColor(Color.YELLOW)
            }

            binding.versionName.text = setVersion()


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
            Toast.makeText(requireContext(), ex.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun sysTheme(checkedTheme: Int) {
        var spM=SharedPreferenceManager(requireActivity())
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Применить тему")
            .setMessage("Вы действительно хотите применить тему? При прослушивании музыки плеер отключится.")
            .setPositiveButton("Да") { _, _ ->
                try {
                    if(PlayerFragment.musicService != null){
                        NowPlaying().clearNP()
                    }
                Toast.makeText(requireContext(),checkedTheme.toString(),Toast.LENGTH_SHORT).show()
                spM.theme=checkedTheme
                AppCompatDelegate.setDefaultNightMode(spM.themeFlag[checkedTheme])
                MainActivity.r=true
                }
                catch (ex: Exception){
                    binding.versionName.text = ex.toString()
                }
            }
            .setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }
        val customDialog = builder.create()
        customDialog.show()
    }

    private fun changeColor(style1: String, selectedColor: Int) {
        try {
            val styleId = resources.getIdentifier(style1, "style", requireContext().packageName)
            val themedContext = ContextThemeWrapper(requireContext(), styleId)
            val themeColorsId = themedContext.resources.getIdentifier("themeColors", "attr", requireContext().packageName)

            if (themeColorsId != 0) {
                val newThemeColorsValue = TypedValue()
                newThemeColorsValue.data = selectedColor
                themedContext.theme.applyStyle(styleId, true)
                themedContext.theme.resolveAttribute(themeColorsId, newThemeColorsValue, true)

                // Update the theme directly in the current activity's window
                requireActivity().window.decorView.setBackgroundColor(newThemeColorsValue.data)
            }
        } catch (ex: Exception) {
            Toast.makeText(requireContext(), ex.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveTheme(index: Int) {
        if (MainActivity.themeIndex != index) {
            var editor = requireContext().getSharedPreferences("THEMES", MODE_PRIVATE).edit()
            editor.putInt("theme", index)
            editor.apply()

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Применить тему")
                .setMessage("Вы действительно хотите применить тему? При прослушивании музыки плеер отключится.")
                .setPositiveButton("Да") { _, _ ->
                    try {
                        // Apply the new theme directly without restarting the app
                        if(PlayerFragment.musicService != null){
                            NowPlaying().clearNP()
                        }
                            MainActivity.themeIndex = index
                            requireActivity().setTheme(MainActivity.currentThemeNav[MainActivity.themeIndex])
                            requireActivity().setResult(Activity.RESULT_OK) // Установка результата
                            requireActivity().invalidateOptionsMenu() // Обновляем меню активити, если оно есть
                            // Пересоздаем активити для отображения изменений темы
                            requireActivity().recreate()
                            MainActivity.r=true
                    }
                    catch (ex: Exception){
                        binding.versionName.text = ex.toString()
                    }
                }
                .setNegativeButton("Нет") { dialog, _ -> dialog.dismiss() }
            val customDialog = builder.create()
            customDialog.show()
        }
    }

    private fun CheckCustomTheme(style: String): Boolean {
        val styleId = resources.getIdentifier(style, "style", requireContext().packageName)
        val themedContext = ContextThemeWrapper(requireContext(), styleId)
        val themeColorsId = themedContext.resources.getIdentifier("themeColors", "attr", requireContext().packageName)
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

    private fun setVersion(): String {
        return "Версия: 1.0"
    }

    // Сохранение выбранного цвета в SharedPreferences
    private fun saveSelectedColor(color: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("selectedColor", color)
        editor.apply()
    }
}