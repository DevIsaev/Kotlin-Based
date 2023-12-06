package com.example.onboardingtest.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.example.onboardingtest.Data
import com.example.onboardingtest.R


class SecondPageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_second_page, container, false)
        val next=view.findViewById<TextView>(R.id.next)
        var viewPager=activity?.findViewById<ViewPager2>(R.id.view_pager)
        var eText=view.findViewById<TextView>(R.id.EMAIL).text
        var pText=view.findViewById<TextView>(R.id.PHONE).text
        var passText=view.findViewById<TextView>(R.id.PASS).text
        next.setOnClickListener {
            if (eText.isEmpty()) {
                view.findViewById<TextView>(R.id.EMAIL).error = "Пустое поле"
            } else if (pText.isEmpty()) {
                view.findViewById<TextView>(R.id.PHONE).error = "Пустое поле"
            } else if (passText.isEmpty()) {
                view.findViewById<TextView>(R.id.PASS).error = "Пустое поле"
            } else {
                Data.userEmail = eText.toString()
                Data.userPhone = pText.toString()
                Data.userPass = passText.toString()
                viewPager?.currentItem = 2
            }
        }
        return  view
    }


}