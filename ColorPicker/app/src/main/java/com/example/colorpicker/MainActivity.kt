package com.example.colorpicker

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import com.example.colorpicker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    lateinit var selectedColor:ColorsObject
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadColorSpinner()
        binding.buttonBG.setBackgroundColor(Color.parseColor(selectedColor.hexHash))
        binding.buttonBG.setOnClickListener {
            // Change the color of the button
            binding.buttonBG.setBackgroundColor(Color.parseColor(selectedColor.hexHash))
        }
    }

    private fun loadColorSpinner()
    {
        selectedColor = ColorList().defaultColor
        binding.colorSpinner.apply {
            adapter = ColorSpinner(applicationContext, ColorList().basicColors())
            setSelection(ColorList().colorPosition(selectedColor), false)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener
            {
                override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long)
                {
                    selectedColor = ColorList().basicColors()[position]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}
            }
        }

    }
}