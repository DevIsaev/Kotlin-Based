package com.example.onboardingtest.screens

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.onboardingtest.Data
import com.example.onboardingtest.R


class ThirdPageFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_third_page, container, false)
        val finish=view.findViewById<TextView>(R.id.finish)
        view.findViewById<TextView>(R.id.nick).text=Data.userName.toString()
        view.findViewById<TextView>(R.id.email).text=Data.userEmail.toString()
        view.findViewById<TextView>(R.id.phone).text=Data.userPhone.toString()
        view.findViewById<TextView>(R.id.pass).text=Data.userPass.toString()
        finish.setOnClickListener {
            if(Data.userName=="НЕ АВТОРИЗОВАН"){
                view.findViewById<TextView>(R.id.nick).text="Пусто"
            }
            else if(Data.userEmail=="НЕ АВТОРИЗОВАН"){
                view.findViewById<TextView>(R.id.email).text="Пусто"
            }
            else if(Data.userPhone=="НЕ АВТОРИЗОВАН"){
                view.findViewById<TextView>(R.id.phone).text="Пусто"
            }
            else if(Data.userPass=="НЕ АВТОРИЗОВАН"){
                view.findViewById<TextView>(R.id.pass).text="Пусто"
            }
            else {
                findNavController().navigate(R.id.action_onBoardingFragment_to_homeFragment)
                finished()
            }
        }
        return view
    }

    private fun finished(){
        var sharedPreferences=requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        var editor=sharedPreferences.edit()
        editor.putBoolean("finished",true)
        editor.apply()

    }
}