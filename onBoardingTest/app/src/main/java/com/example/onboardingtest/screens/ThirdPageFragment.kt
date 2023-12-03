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
import com.example.onboardingtest.R


class ThirdPageFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view= inflater.inflate(R.layout.fragment_third_page, container, false)
        val finish=view.findViewById<TextView>(R.id.textView2)
        finish.setOnClickListener { findNavController().navigate(R.id.action_onBoardingFragment_to_homeFragment)
        finished()
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