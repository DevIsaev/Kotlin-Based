package com.example.musicplayerbasics

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.musicplayerbasics.databinding.FragmentColumnStatisticBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF

class ColumnStatistic : Fragment() {
    private lateinit var binding: FragmentColumnStatisticBinding
    private var profitValue = ArrayList<BarEntry>()
    private lateinit var arr:ArrayList<Music>
    companion object {
        fun newInstance(): ColumnStatistic {
            return ColumnStatistic()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentColumnStatisticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arr=Statistics.arrayList
        loadMusicCounts(arr,context!!)
        dataListing()
        setChart()

        // Добавляем обработчик нажатия на значения BarEntry
        binding.chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e is BarEntry) {
                    val entry = e as BarEntry
                    // Показываем Toast с названием и значением
                    Toast.makeText(context, "Value: ${entry.x} ${entry.y}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected() {
                // Do nothing
            }
        })
    }
    override fun onResume() {
        super.onResume()
        arr=Statistics.arrayList
        loadMusicCounts(arr,context!!)
    }
    private fun dataListing() {
        arr.sortByDescending { it.count } // Сортируем список по убыванию count

        // Отображаем только топ-10 композиций
        for (i in 0 until minOf(10, arr.size)) {
            val music = arr[i]
            if (music.count==0)
            {
                break
            }
            else{
                profitValue.add(BarEntry((i + 1).toFloat(), music.count.toFloat())) // Устанавливаем позицию и количество прослушиваний
            }
        }

    }

    private fun setChart() {
        // Получение текущей темы приложения
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isNightMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES

        binding.chart.description.isEnabled = false
        binding.chart.setMaxVisibleValueCount(100)
        binding.chart.setPinchZoom(false)
        binding.chart.setDrawBarShadow(false)
        binding.chart.setDrawGridBackground(false)

        var xLabels = ArrayList<String>()
        // Создаем список названий композиций и исполнителей
        for (i in 0 until minOf(10, arr.size)) {
            val music = arr[i]
            if (music.count==0)
            {
                break
            }
            else{
                xLabels.add("${music.title}".take(10))
            }
        }
        val xAx = binding.chart.xAxis
        xAx.setDrawGridLines(false)
        xAx.granularity = 1f
        xAx.textSize = 1f
        xAx.isGranularityEnabled = true
        xAx.valueFormatter = IndexAxisValueFormatter(xLabels) // Используем список названий композиций и исполнителей в качестве форматтера оси X
        xAx.setCenterAxisLabels(true)
        xAx.textColor= if (isNightMode) Color.WHITE else Color.BLACK// Метки будут по центру между столбцами
        binding.chart.axisLeft.setDrawGridLines(false)
        binding.chart.legend.isEnabled = true

        var barDataSetter: BarDataSet
        if (binding.chart.data != null && binding.chart.data.dataSetCount > 0) {
            barDataSetter = binding.chart.data.getDataSetByIndex(0) as BarDataSet
            barDataSetter.values = profitValue
            binding.chart.data.notifyDataChanged()
            binding.chart.notifyDataSetChanged()
        } else {
            barDataSetter = BarDataSet(profitValue, "Data Set")
            barDataSetter.setColors(*ColorTemplate.MATERIAL_COLORS)
            //barDataSetter.valueTextColor
            barDataSetter.setDrawValues(false)
            barDataSetter.iconsOffset = MPPointF(10f, 10f)
            val dataSet = ArrayList<IBarDataSet>()
            dataSet.add(barDataSetter)
            val data = BarData(dataSet)
            binding.chart.data = data
            binding.chart.setFitBars(true)
        }
        binding.chart.invalidate()
    }
}