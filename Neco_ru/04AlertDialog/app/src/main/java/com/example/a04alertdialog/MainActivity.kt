package com.example.a04alertdialog

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //CreateDialog()
        MultiChoiseAlertDialog()
    }
    private fun CreateDialog(){
            val builder= AlertDialog.Builder(this)
            builder.setTitle("Alert Dialog")
            builder.setMessage("ЭТО СООБЩЕНИЕ ALERT DIALOG")
            builder.setNeutralButton("Neutral",DialogInterface.OnClickListener { dialogInterface, i ->

            })
            builder.setNegativeButton("Negative",DialogInterface.OnClickListener { dialogInterface, i ->

            })
            builder.setPositiveButton("Positive",DialogInterface.OnClickListener { dialogInterface, i ->

            })
            builder.show()
    }
    private fun MultiChoiseAlertDialog(){
        val builder= AlertDialog.Builder(this)
        builder.setTitle("MultiChoise Dialog ВЫБЕРИ ЧТО ТО")
        builder.setMultiChoiceItems(R.array.multi_item,null){dialog,wich,chose ->
            Toast.makeText(this,"вы выбрали - ${wich},${chose}",Toast.LENGTH_SHORT).show()
        }
        builder.setNeutralButton("Info",DialogInterface.OnClickListener { dialogInterface, i ->
            Toast.makeText(this,"просто что то выбери",Toast.LENGTH_SHORT).show()
        })
        builder.setNegativeButton("No",DialogInterface.OnClickListener { dialogInterface, i ->
            Toast.makeText(this,"ну и пошел ты",Toast.LENGTH_SHORT).show()
        })
        builder.setPositiveButton("Yes",DialogInterface.OnClickListener { dialogInterface, i ->
            Toast.makeText(this,"да ",Toast.LENGTH_SHORT).show()
        })
        builder.show()
    }
}