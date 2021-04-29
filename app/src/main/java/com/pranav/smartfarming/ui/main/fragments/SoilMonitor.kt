package com.pranav.smartfarming.ui.main.fragments

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.firestore.FirebaseFirestore
import com.pranav.smartfarming.R
import com.pranav.smartfarming.dataClasses.SoilData
import com.pranav.smartfarming.databinding.DialogPredictedCropBinding
import com.pranav.smartfarming.databinding.FragmentSoilMonitorBinding
import com.pranav.smartfarming.utils.errorToast
import com.squareup.okhttp.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class SoilMonitor : Fragment() {

    lateinit var binding: FragmentSoilMonitorBinding

    private val fireStore = FirebaseFirestore.getInstance()
    private var soilData: SoilData? = null
    private val docRef = fireStore.collection("data")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSoilMonitorBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getValues()

        binding.soilMonitorRefresh.setOnRefreshListener {
            getValues()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getValues() {
        docRef.get().addOnCompleteListener {
            binding.soilMonitorRefresh.isRefreshing = false

            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
            val formatted = current.format(formatter)
            binding.soilDataLastUpdated.text = "Last updated: $formatted"
            val soilData = it.result?.toObjects(SoilData::class.java)
            binding.data = soilData?.first()
            this.soilData = soilData?.firstOrNull()
        }
            .addOnFailureListener {
                binding.soilMonitorRefresh.isRefreshing = false
                it.message?.let { it1 -> context?.errorToast(it1) }
            }

        binding.soilPredictCrop.setOnClickListener {
            binding.soilPredictCrop.text = "Getting predictions"
            it.isEnabled = false

            getPrediction()
        }
    }

    private fun getPrediction() {
        val client = OkHttpClient()

        val formBody: RequestBody = FormEncodingBuilder()
            .add("N", soilData?.N!!)
            .add("P", soilData?.P!!)
            .add("K", soilData?.K!!)
            .add("temperature", soilData?.temperature!!)
            .add("humidity", soilData?.humidity!!)
            .add("pH", soilData?.pH!!)
            .add("moisture", soilData?.moisture!!)
            .build()


        val get: Request = Request.Builder()
            .url("http://192.168.10.72:5000/predict_mobile")
            .post(formBody)
            .build()

        client.newCall(get).enqueue(object : Callback {

            override fun onFailure(request: Request?, e: java.io.IOException?) {
                e?.printStackTrace()

                requireActivity().runOnUiThread {
                    requireActivity().errorToast(e?.message.toString())
                    binding.soilPredictCrop.text = "Predict Crop"
                    binding.soilPredictCrop.isEnabled = true
                }

            }

            override fun onResponse(response: Response?) {
                try {
                    val responseBody: String? = response?.body()?.string()
                    if (response?.isSuccessful == false) {
                        requireActivity().runOnUiThread {
                            requireActivity().errorToast(response.message().toString())
                            binding.soilPredictCrop.text = "Predict Crop"
                            binding.soilPredictCrop.isEnabled = true
                            Log.d(TAG, "False")
                        }

                    } else {
                        binding.soilPredictCrop.text = "Predict Crop"
                        requireActivity().runOnUiThread {
                            val alertDialog = AlertDialog.Builder(requireContext()).create()
                            alertDialog.setCancelable(true)
                            val mDialogBinding = DialogPredictedCropBinding.inflate(layoutInflater)
                            alertDialog.setView(mDialogBinding.root)

                            mDialogBinding.predictedCropName.text = responseBody.toString()
                            mDialogBinding.cropPredictDetails.setOnClickListener {
                                alertDialog.dismiss()
//                                findNavController().navigate(
//                                    R.id.cropPredictionFragment,
//                                    bundleOf("" to "")
//                                )
                            }

                            alertDialog.setOnDismissListener {
                                binding.soilPredictCrop.isEnabled = true
                            }
                            alertDialog.show()
                        }

                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

    }

    companion object {
        const val TAG = "soil_fragment"
    }
}