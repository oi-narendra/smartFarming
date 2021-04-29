package com.pranav.smartfarming.ui.main.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pranav.smartfarming.R
import com.pranav.smartfarming.dataClasses.SampleData
import com.pranav.smartfarming.databinding.FragmentSamplesBinding
import com.squareup.okhttp.*
import timber.log.Timber


class SamplesFragment : Fragment(), SamplesAdapter.OnSampleClickListener {

    lateinit var binding: FragmentSamplesBinding

    private val samplesList = mutableListOf<SoilSampleDataNameDate>()
    private var fetchedList = HashMap<String, SampleData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSamplesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getSamples()

        binding.samplesRecycler.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = SamplesAdapter(this@SamplesFragment, samplesList)
        }
    }

    private fun getSamples() {
        val client = OkHttpClient()

        val get: Request = Request.Builder()
            .url("https://smart-farming-edcf5-default-rtdb.firebaseio.com/Soil%20Sample.json")
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

                    val list = Gson().fromJson<HashMap<String, SampleData>>(
                        responseBody,
                        object : TypeToken<HashMap<String, SampleData>>() {}.type
                    )

                    fetchedList = list
                    samplesList.clear()
                    if (fetchedList.isNotEmpty()) {

                        fetchedList.entries.forEach {
                            samplesList.add(
                                SoilSampleDataNameDate(
                                    name = it.value.name,
                                    date = it.value.Date
                                )
                            )
                        }
                        requireActivity().runOnUiThread {
                            binding.samplesRecycler.adapter?.notifyDataSetChanged()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Timber.d(e.message.toString())
                    Timber.d(e.localizedMessage.toString())
                }
            }
        })

    }

    override fun onSampleItemClicked(position: Int) {
        findNavController().navigate(
            R.id.sampleDetailFragment,
            bundleOf("sample" to fetchedList[samplesList[position].name])
        )
    }

    data class SoilSampleDataNameDate(val name: String, val date: String)

}