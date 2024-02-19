package com.example.musicplayerbasics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.musicplayerbasics.databinding.FragmentAllMusicBinding


class AllMusicFragment : Fragment() {
    private lateinit var binding: FragmentAllMusicBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var musicAdapter: AdapterMusicList
    private lateinit var totalSongsTextView: TextView
    private var musicList = ArrayList<Music>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAllMusicBinding.inflate(inflater, container, false)
        recyclerView = binding.listMusic
        totalSongsTextView = binding.totalSongs

        // Настройка RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Инициализация адаптера, если он не был инициализирован ранее
        if (!::musicAdapter.isInitialized) {
            musicAdapter = AdapterMusicList(requireContext(), musicList)
            recyclerView.adapter = musicAdapter
        }

        // Установка общего количества песен
        totalSongsTextView.text = "Всего песен: ${musicList.size}"
        return binding.root
    }

    // Метод для установки списка музыки и обновления адаптера
    fun setMusicList(musicList: ArrayList<Music>) {
        if (!::musicAdapter.isInitialized) {
            musicAdapter = AdapterMusicList(requireContext(), musicList)
            recyclerView.adapter = musicAdapter
        } else {
            this.musicList.clear()
            this.musicList.addAll(musicList)
            musicAdapter.notifyDataSetChanged()
        }
        totalSongsTextView.text = "Всего песен: ${musicList.size}"
    }
}