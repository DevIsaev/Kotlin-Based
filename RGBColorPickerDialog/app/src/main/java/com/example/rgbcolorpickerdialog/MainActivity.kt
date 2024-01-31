package com.example.rgbcolorpickerdialog

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IntegerRes
import com.example.rgbcolorpickerdialog.databinding.ActivityMainBinding
import com.example.rgbcolorpickerdialog.databinding.RgbLayoutDialogBinding
import com.example.rgbcolorpickerdialog.databinding.SmartColorpickerFromImgBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
//1
private val rgbLayoutDialogBinding : RgbLayoutDialogBinding by lazy {
    RgbLayoutDialogBinding.inflate(layoutInflater)
}
//2
private lateinit var bitmap:Bitmap
private var selectedColor: Int = Color.WHITE
private val  smartColorpickerFromImgBinding:SmartColorpickerFromImgBinding  by lazy {
    SmartColorpickerFromImgBinding.inflate(layoutInflater)
}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //1
        val rgbDialog=Dialog(this).apply {
            setContentView(rgbLayoutDialogBinding.root)
            window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
            setCancelable(false)
        }
        setOnSeekbar("R", rgbLayoutDialogBinding.RLayout.typeTxt, rgbLayoutDialogBinding.RLayout.seekBar, rgbLayoutDialogBinding.RLayout.colorValueTxt)
        setOnSeekbar("G", rgbLayoutDialogBinding.GLayout.typeTxt, rgbLayoutDialogBinding.GLayout.seekBar, rgbLayoutDialogBinding.GLayout.colorValueTxt)
        setOnSeekbar("B", rgbLayoutDialogBinding.BLayout.typeTxt, rgbLayoutDialogBinding.BLayout.seekBar, rgbLayoutDialogBinding.BLayout.colorValueTxt)
        rgbLayoutDialogBinding.cancelBTN.setOnClickListener {
            rgbDialog.dismiss()
        }
        rgbLayoutDialogBinding.okBTN.setOnClickListener {
            binding.color.text = setRGBColor()
            binding.color.setTextColor(Color.parseColor(setRGBColor()))
            rgbDialog.dismiss()
        }
        binding.color.setOnClickListener {
            rgbDialog.show()
        }

        //2
        val smartDialog=Dialog(this).apply {
            setContentView(smartColorpickerFromImgBinding.root)
            window!!.setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
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
        smartColorpickerFromImgBinding.okBTN.setOnClickListener {
            binding.layout.setBackgroundColor(selectedColor)
            smartDialog.dismiss()
        }
        smartColorpickerFromImgBinding.cancelBTN.setOnClickListener {
            smartDialog.dismiss()
        }
        binding.buttonSmartCP.setOnClickListener {
            smartDialog.show()
        }
    }

    private fun setOnSeekbar(type:String, typeTxt:TextView,seekBar: SeekBar,colorTxt:TextView) {
        typeTxt.text = type
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                colorTxt.text = seekBar.progress.toString()
                setRGBColor()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}

        })
        colorTxt.text = seekBar.progress.toString()
    }
    private fun setRGBColor():String {
        var hex=String.format(
            "#%02x%02x%02x",
            rgbLayoutDialogBinding.RLayout.seekBar.progress,
            rgbLayoutDialogBinding.GLayout.seekBar.progress,
            rgbLayoutDialogBinding.BLayout.seekBar.progress,
        )
        rgbLayoutDialogBinding.colorView.setBackgroundColor(Color.parseColor(hex))
        return hex
    }
}