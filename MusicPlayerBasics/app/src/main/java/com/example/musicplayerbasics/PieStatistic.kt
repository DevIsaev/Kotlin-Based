package com.example.musicplayerbasics

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.musicplayerbasics.databinding.FragmentPieStatisticBinding
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.MPPointF


class PieStatistic : Fragment() {
    private lateinit var binding: FragmentPieStatisticBinding
    private var profitValue = ArrayList<PieEntry>()
    private lateinit var arr:ArrayList<Music>
    companion object {
        fun newInstance(): PieStatistic {
            return PieStatistic()
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPieStatisticBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arr=Statistics.arrayList
        loadMusicCounts(arr,context!!)
        dataListing()
        setChart()
        // Добавляем обработчик нажатия на значение PieEntry
        binding.chart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e is PieEntry) {
                    val entry = e as PieEntry
                    // Показываем Toast с названием и значением
                    Toast.makeText(context, "${entry.label}: ${entry.value}", Toast.LENGTH_SHORT).show()
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
            var titleArtist="${music.artist} - ${music.title}"
            if (music.count==0)
            {
                break
            }
            else{
                profitValue.add(PieEntry(music.count.toFloat(),titleArtist.take(25))) // Устанавливаем позицию и количество прослушиваний
            }
        }
    }

    private fun setChart() {
        // Получение текущей темы приложения
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val isNightMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES

        val pieDataSetter = PieDataSet(profitValue, "Pie Set")
        pieDataSetter.colors = ColorTemplate.JOYFUL_COLORS.asList()
        pieDataSetter.setDrawValues(true)
        pieDataSetter.sliceSpace = 3f
        pieDataSetter.iconsOffset = MPPointF(10f, 10f)
        pieDataSetter.selectionShift = 10f

        val dataSet = ArrayList<IPieDataSet>()
        dataSet.add(pieDataSetter)

        val data = PieData(pieDataSetter)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(12f)
        data.setValueTextColor(Color.RED)
        binding.chart.data = data
        binding.chart.description.isEnabled = false

        binding.chart.animateY(1400, Easing.EaseInOutQuad)
        binding.chart.setEntryLabelColor(if (isNightMode) Color.WHITE else Color.BLACK)
        binding.chart.holeRadius = 13f
        binding.chart.setTransparentCircleColor(Color.GRAY)
        binding.chart.setTransparentCircleAlpha(110)
        binding.chart.transparentCircleRadius = 7f
        binding.chart.setUsePercentValues(true)
        binding.chart.legend.isEnabled=false
//        val legend: Legend = binding.chart.legend
//        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
//        legend.orientation = Legend.LegendOrientation.VERTICAL
//        legend.setDrawInside(false)
//        legend.xEntrySpace = 2f
//        legend.yEntrySpace = 2f
//        legend.yOffset = 0f
//        legend.textSize = 18f


    // Установка цвета текста в зависимости от темы
//        legend.textColor = if (isNightMode) Color.WHITE else Color.BLACK
        binding.chart.invalidate()
    }
}