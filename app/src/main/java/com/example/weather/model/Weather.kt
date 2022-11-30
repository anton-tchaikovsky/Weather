package com.example.weather.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Weather(
    val cityName:String,
    val temperature: Double,
    val condition: String
) : Parcelable
