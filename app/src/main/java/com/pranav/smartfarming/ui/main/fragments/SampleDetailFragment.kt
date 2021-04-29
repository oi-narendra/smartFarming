package com.pranav.smartfarming.ui.main.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.pranav.smartfarming.dataClasses.SampleData
import com.pranav.smartfarming.databinding.FragmentSampleDetailBinding
import timber.log.Timber

private const val ARG_PARAM1 = "sample"

class SampleDetailFragment : Fragment() {
    private var sampleData: SampleData? = null
    lateinit var binding: FragmentSampleDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sampleData = it.getSerializable(ARG_PARAM1) as SampleData
        }

        Timber.d(sampleData.toString())

        sampleData?.let {
            requireActivity().actionBar?.title = it.name
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSampleDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.soilSamplePredictedCropName.text = sampleData?.Predicted_Crop ?: "-"

        val moistureDataSet = LineDataSet(getDataValues("Moisture"), "Moisture").also {
            it.color = Color.RED;it.setDrawCircles(false)
        }
        val moistureBarDataSet = BarDataSet(getBarDataValues("Moisture"), "Moisture").also {
            it.color = Color.RED
        }

        val temperatureDataset = LineDataSet(getDataValues("Temperature"), "Temperature").also {
            it.color = Color.BLUE;it.setDrawCircles(false)
        }
        val temperatureBarDataset =
            BarDataSet(getBarDataValues("Temperature"), "Temperature").also {
                it.color = Color.BLUE
            }

        val humidityDataSet = LineDataSet(getDataValues("Humidity"), "Humidity").also {
            it.color = Color.GREEN;it.setDrawCircles(false)
        }
        val humidityBarDataSet = BarDataSet(getBarDataValues("Humidity"), "Humidity").also {
            it.color = Color.GREEN
        }

        val nitrogenDataSet = LineDataSet(getDataValues("N"), "N").also {
            it.color = Color.YELLOW;it.setDrawCircles(false)
        }
        val nitrogenBarDataSet = BarDataSet(getBarDataValues("N"), "N").also {
            it.color = Color.YELLOW
        }

        val phosphorousDataSet = LineDataSet(getDataValues("P"), "P").also {
            it.color = Color.CYAN;it.setDrawCircles(false)
        }
        val phosphorousBarDataSet = BarDataSet(getBarDataValues("P"), "P").also {
            it.color = Color.CYAN
        }

        val potassiumDataSet = LineDataSet(getDataValues("K"), "K").also {
            it.color = Color.GRAY;it.setDrawCircles(false)
        }
        val potassiumBarDataSet = BarDataSet(getBarDataValues("K"), "K").also {
            it.color = Color.GRAY
        }

        val phDataSet = LineDataSet(getDataValues("pH"), "pH").also {
            it.color = Color.MAGENTA;it.setDrawCircles(false)
        }
        val phBarDataSet = BarDataSet(getBarDataValues("pH"), "pH").also {
            it.color = Color.MAGENTA;
        }


        val lineDataSets = mutableListOf<LineDataSet>()
        val barDataSets = mutableListOf<BarDataSet>()

        lineDataSets.add(moistureDataSet)
        lineDataSets.add(temperatureDataset)
        lineDataSets.add(humidityDataSet)
        lineDataSets.add(nitrogenDataSet)
        lineDataSets.add(phosphorousDataSet)
        lineDataSets.add(potassiumDataSet)
        lineDataSets.add(phDataSet)


        barDataSets.add(moistureBarDataSet)
        barDataSets.add(temperatureBarDataset)
        barDataSets.add(humidityBarDataSet)
        barDataSets.add(nitrogenBarDataSet)
        barDataSets.add(phosphorousBarDataSet)
        barDataSets.add(potassiumBarDataSet)
        barDataSets.add(phBarDataSet)

        Timber.d(lineDataSets.toString())

        binding.chart.setBackgroundColor(Color.WHITE)
        val lineData = LineData(lineDataSets as List<ILineDataSet>?)
        val barData = BarData(barDataSets as List<IBarDataSet>?)
        binding.chart.apply {
            xAxis.axisMinimum = 0F
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1F
            axisRight.isEnabled = false
            data = lineData
            invalidate()

        }

        binding.barChart.apply {
            xAxis.axisMinimum = 0F
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1F
            axisRight.isEnabled = false
            data = barData
            invalidate()

        }

    }

    private fun getDataValues(key: String): MutableList<Entry> {
        val dataVals = mutableListOf<Entry>()
        sampleData?.readings?.entries?.forEachIndexed { index, mutableEntry ->
            Timber.d(mutableEntry.value.toString())
            dataVals.add(
                Entry(
                    index.toFloat(), when (key) {
                        "Moisture" -> mutableEntry.value.Moisture
                        "Temperature" -> mutableEntry.value.Temperature
                        "Humidity " -> mutableEntry.value.Humidity
                        "N" -> mutableEntry.value.Nitrogen
                        "P" -> mutableEntry.value.Phosphorous
                        "K" -> mutableEntry.value.Potassium
                        "pH" -> mutableEntry.value.pH
                        else -> 0F
                    }
                )
            )
        }
        return dataVals
    }

    private fun getBarDataValues(key: String): MutableList<BarEntry> {
        val dataVals = mutableListOf<BarEntry>()
        sampleData?.readings?.entries?.forEachIndexed { index, mutableEntry ->
            Timber.d(mutableEntry.value.toString())
            dataVals.add(
                BarEntry(
                    index.toFloat(), when (key) {
                        "Moisture" -> mutableEntry.value.Moisture
                        "Temperature" -> mutableEntry.value.Temperature
                        "Humidity " -> mutableEntry.value.Humidity
                        "N" -> mutableEntry.value.Nitrogen
                        "P" -> mutableEntry.value.Phosphorous
                        "K" -> mutableEntry.value.Potassium
                        "pH" -> mutableEntry.value.pH
                        else -> 0F
                    }
                )
            )
        }
        return dataVals
    }
}