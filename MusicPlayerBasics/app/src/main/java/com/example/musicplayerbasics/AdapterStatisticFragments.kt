package com.example.musicplayerbasics

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class AdapterStatisticFragments(var fragmentList:ArrayList<Fragment>, manager: FragmentManager, lifecycle: Lifecycle):
    FragmentStateAdapter(manager,lifecycle) {
    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}