package com.pranav.smartfarming.dataClasses

data class PredictedCropModel(
    val attributes: Attributes,
    val message: String,
    val predicted_crop: String
)
