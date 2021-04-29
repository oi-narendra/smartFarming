package com.pranav.smartfarming.ui.main.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.pranav.smartfarming.R
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


        val temperatureDataset = LineDataSet(getDataValues("Temperature"), "Temperature").also {
            it.color = Color.BLUE;it.setDrawCircles(false)
        }

        val humidityDataSet = LineDataSet(getDataValues("Humidity"), "Humidity").also {
            it.color = Color.GREEN;it.setDrawCircles(false)
        }


        val nitrogenDataSet = LineDataSet(getDataValues("N"), "N").also {
            it.color =
                ContextCompat.getColor(requireContext(), R.color.pink);it.setDrawCircles(false)
        }


        val phosphorousDataSet = LineDataSet(getDataValues("P"), "P").also {
            it.color = Color.CYAN;it.setDrawCircles(false)
        }


        val potassiumDataSet = LineDataSet(getDataValues("K"), "K").also {
            it.color = Color.GRAY;it.setDrawCircles(false)
        }


        val dates = mutableListOf<String>()

        sampleData?.readings?.entries?.forEach {
            dates.add(it.value.Date)
        }



        Timber.d(dates.toString())

        val moistureDataSets = mutableListOf<LineDataSet>()
        val npkDataSets = mutableListOf<LineDataSet>()
        val humidityDataSets = mutableListOf<LineDataSet>()

        moistureDataSets.add(moistureDataSet)

        npkDataSets.add(nitrogenDataSet)
        npkDataSets.add(phosphorousDataSet)
        npkDataSets.add(potassiumDataSet)

        humidityDataSets.add(temperatureDataset)
        humidityDataSets.add(humidityDataSet)

        binding.moistureChart.setBackgroundColor(Color.WHITE)
        binding.npkChart.setBackgroundColor(Color.WHITE)
        binding.temperatureChart.setBackgroundColor(Color.WHITE)
        val lineMoistureData = LineData(moistureDataSets as List<ILineDataSet>?)
        val lineTempData = LineData(humidityDataSets as List<ILineDataSet>?)
        val lineNPKData = LineData(npkDataSets as List<ILineDataSet>?)

        binding.moistureChart.apply {
            xAxis.axisMinimum = 0F
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1F
            axisRight.isEnabled = false
            data = lineMoistureData

            val xAxis = xAxis
            xAxis.setDrawAxisLine(true)
            xAxis.setDrawGridLines(false)
            xAxis.setDrawLabels(true)
            xAxis.spaceMax = 1f // optional
            xAxis.spaceMin = 1f // optional
//            xAxis.valueFormatter = object : ValueFormatter() {
//                override
//                fun getFormattedValue(value: Float): String {
//                    Timber.d(value.toString())
//
//                    if (value.toInt() > dates.size) return "-"
//                    return dates[value.toInt()]
//                }
//            }
            invalidate()

        }

        binding.temperatureChart.apply {
            xAxis.axisMinimum = 0F
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1F
            axisRight.isEnabled = false
            data = lineTempData
            invalidate()

        }
        binding.npkChart.apply {
            xAxis.axisMinimum = 0F
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1F
            axisRight.isEnabled = false
            data = lineNPKData
            invalidate()

        }


    }

    private fun getDataValues(key: String): MutableList<Entry> {
        val dataVals = mutableListOf<Entry>()
        sampleData?.readings?.entries?.forEachIndexed { index, mutableEntry ->
            dataVals.add(
                Entry(
                    index.toFloat(), when (key) {
                        "Moisture" -> mutableEntry.value.Moisture
                        "Temperature" -> mutableEntry.value.Temperature
                        "Humidity" -> mutableEntry.value.Humidity
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