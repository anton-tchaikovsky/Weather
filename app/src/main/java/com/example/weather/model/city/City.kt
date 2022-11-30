package com.example.weather.model.city

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class City(
    val cityName: String,
    val lat: Double,
    val lon: Double
):Parcelable
