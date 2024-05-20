package com.example.musicplayerbasics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.musicplayerbasics.databinding.FragmentAllMusicStatisticBinding

class AllMusicStatistic : Fragment() {
    private lateinit var binding: FragmentAllMusicStatisticBinding
    private lateinit var adapter:AdapterMusicList
    private lateinit var arr:ArrayList<Music>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAllMusicStatisticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arr=Statistics.arrayList
        loadMusicCounts(arr,context!!)
        arr.sortByDescending { it.count }
        binding.listMusicStatistic.setHasFixedSize(true)
        binding.listMusicStatistic.setItemViewCacheSize(100)
        binding.listMusicStatistic.layoutManager = LinearLayoutManager(requireContext())
        //binding.listMusicStatistic.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
        adapter= AdapterMusicList(requireContext(),arr,false,false,true)
        binding.listMusicStatistic.adapter = adapter

    }

    override fun onResume() {
        super.onResume()
        arr=Statistics.arrayList
        loadMusicCounts(arr,context!!)
        arr.sortByDescending { it.count }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}