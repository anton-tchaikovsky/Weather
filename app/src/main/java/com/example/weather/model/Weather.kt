package com.example.weather.model

import android.os.Parcelable
import com.example.weather.model.city.City
import com.example.weather.model.city.getDefaultCity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Weather(
    val city: City = getDefaultCity(),
    val temperature: Int = 15,
    val feelsLike: Int = 9
) : Parcelable
