package com.pranav.smartfarming.ui.main.fragments.crops

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pranav.smartfarming.R
import com.pranav.smartfarming.dataClasses.Attributes
import com.pranav.smartfarming.dataClasses.PredictedCropModel
import com.pranav.smartfarming.databinding.FragmentCropsBinding
import com.pranav.smartfarming.ui.main.MainActivity
import timber.log.Timber


class CropsFragment : Fragment() {

    lateinit var binding: FragmentCropsBinding

    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var attributes: Attributes
    private lateinit var remoteAttributes: Attributes

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCropsBinding.inflate(layoutInflater)
        sharedPreferences =
            requireActivity().getSharedPreferences("smart_farming", Context.MODE_PRIVATE)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val predictedCropString = sharedPreferences.getString("active_crop", null)


        if (predictedCropString == null) {
            binding.imageCrop.visibility = View.GONE
            binding.cropNameText.visibility = View.GONE
            binding.cropName.visibility = View.GONE
            binding.soilStatistics.visibility = View.GONE
            binding.scrollview2.visibility = View.GONE
            binding.noCropText.visibility = View.VISIBLE
        } else {
            binding.noCropText.visibility = View.GONE
            val predictedCrop = Gson().fromJson<PredictedCropModel>(
                predictedCropString,
                object : TypeToken<PredictedCropModel>() {}.type
            )

            Timber.d(predictedCrop.toString())

            binding.cropName.text = predictedCrop.predicted_crop
            attributes = predictedCrop.attributes

            setData(attributes)

            val database = FirebaseDatabase.getInstance()
            database.getReference("crop")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        val newAttrib = Gson().fromJson<Attributes>(
                            snapshot.value.toString(),
                            object : TypeToken<Attributes>() {}.type
                        )

                        if (newAttrib != null) {
                            remoteAttributes = newAttrib
                            setData(remoteAttributes)
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }


    }

    private fun setData(attribs: Attributes) {
        binding.nValue.text = attribs.N.toString()
        binding.pValue.text = attribs.P.toString()
        binding.kValue.text = attribs.K.toString()
        binding.humidityValue.text = attribs.humidity.toString()
        binding.temperatureValue.text = attribs.temperature.toString()
        binding.moistureValue.text = attribs.moisture.toString()
        binding.pHValue.text = attribs.pH.toString()

        Timber.d("attrib: ${attribs.moisture}, attributes: ${attributes.moisture}")
        if (attributes.moisture - attribs.moisture > 40) {
            showIrrigationNotification()
            binding.moistureContainer.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.pink))
        } else {
            binding.moistureContainer.backgroundTintList = null
        }

    }

    private fun showIrrigationNotification() {

        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent =
            PendingIntent.getActivity(requireContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT)
        val channelId = "Default"
        val builder = NotificationCompat.Builder(requireContext(), channelId)
            .setSmallIcon(R.drawable.ic_logo)
            .setColor(ContextCompat.getColor(requireContext(), R.color.color_primary))
            .setContentTitle("Smart Farming")
            .setContentText("Your plant needs water.")
            .setAutoCancel(false)
            .setContentIntent(pendingIntent)

        val manager =
            requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }

        manager.notify(0, builder.build())
    }

}