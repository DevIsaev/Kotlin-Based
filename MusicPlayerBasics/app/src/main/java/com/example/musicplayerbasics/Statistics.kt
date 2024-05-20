package com.example.musicplayerbasics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.musicplayerbasics.databinding.FragmentStatisticsBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.tabs.TabLayoutMediator

class Statistics : BottomSheetDialogFragment() {
    companion object {
        private lateinit var binding: FragmentStatisticsBinding
        lateinit var arrayList: ArrayList<Music>
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bottomSheet: FrameLayout =
            dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
        // Height of the view
        bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        // Behavior of the bottom sheet
        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.apply {
            peekHeight = resources.displayMetrics.heightPixels // Pop-up height
            state = BottomSheetBehavior.STATE_EXPANDED // Expanded state
            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }
        arrayList=MainActivity.MusicListMA

        val fragments =arrayListOf(ColumnStatistic(),PieStatistic(),AllMusicStatistic())
        binding.apply {
            pager.adapter = AdapterStatisticFragments(fragments, childFragmentManager, lifecycle)

            TabLayoutMediator(tabLayout, pager) { tab, position ->
                when (position) {
                    0 -> tab.text = "Топ 10"
                    1 -> tab.text = "Предпочитаемые"
                    2 -> tab.text = "По количеству"
                }
            }.attach()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        arrayList.sortByDescending { it.id } // Сортируем список по убыванию count
    }
}