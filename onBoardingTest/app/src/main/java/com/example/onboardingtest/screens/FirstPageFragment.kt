package com.example.onboardingtest.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.example.onboardingtest.Data
import com.example.onboardingtest.R


class FirstPageFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_first_page, container, false)

        val next=view.findViewById<TextView>(R.id.n)
        var viewPager=activity?.findViewById<ViewPager2>(R.id.view_pager)
        var nText=view.findViewById<TextView>(R.id.Nickname).text
        next.setOnClickListener {
            if (nText.isEmpty()){
                view.findViewById<TextView>(R.id.Nickname).error="Пустое поле"
            }
            else{
                Data.userName=nText.toString()
                viewPager?.currentItem=1
            }
        }



        return  view
    }


}