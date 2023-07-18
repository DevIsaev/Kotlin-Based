package com.example.todo_list

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.begin
import kotlinx.android.synthetic.main.activity_main.end
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val data = findViewById<EditText>(R.id.userData)
        val button=findViewById<Button>(R.id.ButtonGo)
        val label:ListView=findViewById(R.id.List)

        val dateBegin=findViewById<EditText>(R.id.begin)
        val dateEnd=findViewById<EditText>(R.id.end)

        //массив и адаптер для listview
        val arr: MutableList<String> = mutableListOf()
        val adapter= ArrayAdapter(this, android.R.layout.simple_list_item_1,arr)
        label.adapter=adapter



        //удаление элемента
label.setOnItemClickListener { adapterView, view, i, l ->
    val t=label.getItemAtPosition(i).toString()
    adapter.remove(t)
    Toast.makeText(this,"Удалено: $t",Toast.LENGTH_LONG).show()
}
        //дата начала
        dateBegin.setOnClickListener {
            setDate1()
        }
        dateEnd.setOnClickListener {
            setDate2()
        }

        button.setOnClickListener {
        if (data.text.isNotEmpty()&&dateBegin.text.isNotEmpty()&&dateEnd.text.isNotEmpty()){
            /*//дата
            val sdf = SimpleDateFormat(" dd/M/yyyy")
            val currentDate = sdf.format(Date())*/
            //val text=data.text.toString().trim()+currentDate.toString()



            val text="Задача ${data.text.trim()}.\nНачало: ${dateBegin.text}\nКонец: ${dateEnd.text}"
            if (text!="") {
                //добавление элемента
                adapter.insert(text,0)
                data.text.clear()
                dateBegin.text.clear()
                dateEnd.text.clear()
            }

        }
            else if (data.text.isEmpty()||dateBegin.text.isEmpty()||dateEnd.text.isEmpty()){
            Toast.makeText(this, "какое то поле пустое", Toast.LENGTH_SHORT).show()
        }
            else if (data.text.toString() == "XBOX"||dateBegin.text.isEmpty()||dateEnd.text.isEmpty()) {
                Toast.makeText(this, "buy Activision!", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun setDate1(){
        val calend=Calendar.getInstance()
        val year=calend.get(Calendar.YEAR)
        val month=calend.get(Calendar.MONTH)
        val day=calend.get(Calendar.DAY_OF_MONTH)

        val dpd=DatePickerDialog(
            this,DatePickerDialog.OnDateSetListener { viev, year, monthOfYear, dayOfMonth
            -> val returnDate="${monthOfYear+1} $dayOfMonth $year"
            val date=CalendarHelper.parseDate(
                "MM dd yyyy","dd/M/yyyy", returnDate
            )
            begin.setText(CalendarHelper.parseDate("MM/dd/yyyy","MMM dd yyyy",date))
            begin.error=null
    },year-30,month,day)
        dpd.show()
}
    private fun setDate2(){
        val calend=Calendar.getInstance()
        val year=calend.get(Calendar.YEAR)
        val month=calend.get(Calendar.MONTH)
        val day=calend.get(Calendar.DAY_OF_MONTH)

        val dpd=DatePickerDialog(
            this,DatePickerDialog.OnDateSetListener { viev, year, monthOfYear, dayOfMonth
                -> val returnDate="${monthOfYear+1} $dayOfMonth $year"
                val date=CalendarHelper.parseDate(
                    "MM dd yyyy","MM/dd/yyyy", returnDate
                )
                end.setText(CalendarHelper.parseDate("MM/dd/yyyy","dd MMM yyyy",date))
                end.error=null
            },year-30,month,day)
        dpd.show()
    }
}