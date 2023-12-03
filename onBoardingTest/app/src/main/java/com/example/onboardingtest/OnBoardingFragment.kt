package com.example.onboardingtest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.onboardingtest.screens.FirstPageFragment
import com.example.onboardingtest.screens.SecondPageFragment
import com.example.onboardingtest.screens.ThirdPageFragment
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import viewPagerAdapter


class OnBoardingFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_on_boarding, container, false)


        val fragmentList= arrayListOf(FirstPageFragment(),SecondPageFragment(),ThirdPageFragment())
        val adapter=viewPagerAdapter(fragmentList,requireActivity().supportFragmentManager,lifecycle)
        val viewPager=view.findViewById<ViewPager2>(R.id.view_pager)
        viewPager.adapter=adapter


        val indicator=view.findViewById<WormDotsIndicator>(R.id.worm_dots_indicator)
        indicator.attachTo(viewPager)

        return view
    }


}