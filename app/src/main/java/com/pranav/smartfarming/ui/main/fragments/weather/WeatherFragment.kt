package com.pranav.smartfarming.ui.main.fragments.weather

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pranav.smartfarming.dataClasses.WeatherResponse
import com.pranav.smartfarming.databinding.FragmentWeatherBinding
import com.squareup.okhttp.Callback
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class WeatherFragment : Fragment() {

    lateinit var binding: FragmentWeatherBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWeatherBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getWeatherData()

    }

    private fun getWeatherData() {
        val client = OkHttpClient()

        val get: Request = Request.Builder()
            .url("https://api.openweathermap.org/data/2.5/weather?q=Kathmandu&appid=ea7e8162e53ce3d0873e2a266a7c71e8")
            .build()

        client.newCall(get).enqueue(object : Callback {

            override fun onFailure(request: Request?, e: java.io.IOException?) {
                e?.printStackTrace()


            }

            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(response: Response?) {
                try {
                    val responseBody: String? = response?.body()?.string()

                    Timber.d(responseBody.toString())

                    val weatherData = Gson().fromJson<WeatherResponse>(
                        responseBody,
                        object : TypeToken<WeatherResponse>() {}.type
                    )
                    requireActivity().runOnUiThread {
                        binding.weatherStatus.text = weatherData.weather.first().description
                        binding.weatherDegree.text =
                            (weatherData.main.temp.roundToInt() - 273).toString() + " °C"
                        binding.weatherLocation.text =
                            "${weatherData.name}/ ${weatherData.sys.country}"
                        binding.tempFeelsLike.text =
                            "Feels like: " + (weatherData.main.feels_like.roundToInt() - 273).toString() + " °C"
                        binding.tempMax.text =
                            "Max Temp: " + (weatherData.main.temp_max.roundToInt() - 273).toString() + " °C"
                        binding.tempMin.text =
                            "Min Temp: " + (weatherData.main.temp_min.roundToInt() - 273).toString() + " °C"
                        binding.pressure.text =
                            "Pressure: " + (weatherData.main.pressure).toString() + "mBar"
                        binding.humidity.text =
                            "Humidity: " + (weatherData.main.humidity).toString() + "%"

//                        binding.sunrise.text =
//                            "Sunrise: " + getDate(weatherData.sys.sunrise.toLong())
//                        binding.sunset.text = "Sunset: " + getDate(weatherData.sys.sunset.toLong())
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                    Timber.d(e.message.toString())
                }
            }
        })

    }

    fun getDate(milliSeconds: Long): String? {
        val dateFormat = "dd/MM/yyyy hh:mm:ss.SSS"
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.ROOT)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

}