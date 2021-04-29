package com.pranav.smartfarming.dataClasses

import java.io.Serializable


data class SampleData(
    val Date: String,
    val Predicted_Crop: String,
    val location: String,
    val name: String,
    val readings: HashMap<String, Reading>
):Serializable

data class Reading(
    val Humidity: Float,
    val Moisture: Float,
    val Nitrogen: Float,
    val Phosphorous: Float,
    val Potassium: Float,
    val Temperature: Float,
    val Date: String,
    val pH: Float,
    val testedby: String
):Serializable