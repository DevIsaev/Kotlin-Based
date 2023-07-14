package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.M_operation
import kotlinx.android.synthetic.main.activity_main.M_result
import kotlinx.android.synthetic.main.activity_main.btn0
import kotlinx.android.synthetic.main.activity_main.btn1
import kotlinx.android.synthetic.main.activity_main.btn2
import kotlinx.android.synthetic.main.activity_main.btn3
import kotlinx.android.synthetic.main.activity_main.btn4
import kotlinx.android.synthetic.main.activity_main.btn5
import kotlinx.android.synthetic.main.activity_main.btn6
import kotlinx.android.synthetic.main.activity_main.btn7
import kotlinx.android.synthetic.main.activity_main.btn8
import kotlinx.android.synthetic.main.activity_main.btn9
import kotlinx.android.synthetic.main.activity_main.btnAC
import kotlinx.android.synthetic.main.activity_main.btnBACK
import kotlinx.android.synthetic.main.activity_main.btnC1
import kotlinx.android.synthetic.main.activity_main.btnC2
import kotlinx.android.synthetic.main.activity_main.btnDrob
import kotlinx.android.synthetic.main.activity_main.btnMinus
import kotlinx.android.synthetic.main.activity_main.btnPlus
import kotlinx.android.synthetic.main.activity_main.btnPoint
import kotlinx.android.synthetic.main.activity_main.btnRawno
import kotlinx.android.synthetic.main.activity_main.btnUmnoj
import net.objecthunter.exp4j.ExpressionBuilder
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn0.setOnClickListener{setTextFields("0")}
        btn1.setOnClickListener{setTextFields("1")}
        btn2.setOnClickListener{setTextFields("2")}
        btn3.setOnClickListener{setTextFields("3")}
        btn4.setOnClickListener{setTextFields("4")}
        btn5.setOnClickListener{setTextFields("5")}
        btn6.setOnClickListener{setTextFields("6")}
        btn7.setOnClickListener{setTextFields("7")}
        btn8.setOnClickListener{setTextFields("8")}
        btn9.setOnClickListener{setTextFields("9")}

        btnMinus.setOnClickListener{setTextFields("-")}
        btnPlus.setOnClickListener{setTextFields("+")}
        btnUmnoj.setOnClickListener{setTextFields("*")}
        btnDrob.setOnClickListener{setTextFields("/")}
        btnC1.setOnClickListener{setTextFields("(")}
        btnC2.setOnClickListener{setTextFields(")")}
        btnPoint.setOnClickListener{setTextFields(".")}

        btnAC.setOnClickListener{
            M_operation.text=""
            M_result.text=""
        }
        btnBACK.setOnClickListener{
            val str=M_operation.text.toString()
            if(str.isNotEmpty()){
                M_operation.text=str.substring(0,str.length-1)
            }
            M_result.text=""
        }

        btnRawno.setOnClickListener{
            try {
                var ex=ExpressionBuilder(M_operation.text.toString()).build()
                val result=ex.evaluate()

                val longResult=result.toLong()
                if (result==longResult.toDouble()){
                    M_result.text=longResult.toString()
                }
                else{
                    M_result.text=result.toString()
                }
            }
            catch (e:Exception){
                Log.d("Error","message: ${e.message}" )
            }
        }
    }


    fun setTextFields(str:String){
        if (M_result.text!=""){
            M_operation.text=M_result.text
        }
        M_operation.append(str)
    }

}