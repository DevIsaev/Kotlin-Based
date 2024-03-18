package com.example.musicplayerbasics

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.appcompat.widget.SwitchCompat


class BassView(private val context: Context, private val parent: ViewGroup?, private val audioEffectManager: AudioEffectManager) {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    private val bassBoost: XBassBoost get() = audioEffectManager.bassBoost

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("BassViewPrefs", Context.MODE_PRIVATE)
    }

    /**
     * Create [View] from given [audioEffectManager]
     */
    fun createView(): View {
        val rootView = layoutInflater.inflate(R.layout.view_bass_boost, parent, false) as ViewGroup

        val bassLinearLayout: LinearLayoutCompat = rootView.findViewById(R.id.view_bass_boost_ll_container_bass_boost)

        initBass(bassLinearLayout)
        initSwitch(rootView)
        return rootView
    }

    /**
     * Create UI Views on [bassBoostContainer] for [bassBoost]
     */
    private fun initBass(bassBoostContainer: LinearLayoutCompat) {
        if (!bassBoost.strengthSupported) return

        val max = bassBoost.maxRecommendedStrength
        val currentStrength = bassBoost.roundedStrength

        val bassView = layoutInflater.inflate(R.layout.item_bass, bassBoostContainer, false)

        val minView: AppCompatTextView = bassView.findViewById(R.id.item_bass_tv_min)
        val savedProgress = sharedPreferences.getInt("SeekBarProgress", currentStrength.toInt())
        minView.text = savedProgress.toString() // Устанавливаем сохраненное значение

        val maxView: AppCompatTextView = bassView.findViewById(R.id.item_bass_tv_max)
        maxView.text = max.toString()

        val seekBar: AppCompatSeekBar = bassView.findViewById(R.id.item_bass_sb_progress)
        seekBar.max = max
        seekBar.progress = savedProgress // Устанавливаем сохраненное значение
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                bassBoost.setStrength(progress.toShort())
                minView.text = progress.toString()

                // Save SeekBar progress
                sharedPreferences.edit().putInt("SeekBarProgress", progress).apply()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        bassBoostContainer.addView(bassView)
    }

    /**
     * Find the [SwitchCompat] from given [view] and connect with [bassBoost]
     */
    private fun initSwitch(view: View) {
        val switch: SwitchCompat = view.findViewById(R.id.view_bass_sc_enable)
        bassBoost.enabled = sharedPreferences.getBoolean("SwitchState", false)
        switch.isChecked = bassBoost.enabled
        switch.setOnCheckedChangeListener { _, isChecked ->
            bassBoost.enabled = isChecked

            // Save Switch state
            sharedPreferences.edit().putBoolean("SwitchState", isChecked).apply()
        }
        bassBoost.addEnableStatusChangeListener {
            switch.isChecked = it
        }
    }
}