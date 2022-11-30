package com.example.weather.model.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Forecast(
    val date: String,
    val date_ts: Double,
    val moon_code: Double,
    val moon_text: String,
    val parts: List<Part>,
    val sunrise: String,
    val sunset: String,
    val week: Double
):Parcelable