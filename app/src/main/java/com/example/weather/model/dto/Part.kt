package com.example.weather.model.dto

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Part(
    val condition: String,
    val daytime: String,
    val feels_like: Double,
    val humidity: Double,
    val icon: String,
    val part_name: String,
    val polar: Boolean,
    val prec_mm: Double,
    val prec_period: Double,
    val prec_prob: Double,
    val pressure_mm: Double,
    val pressure_pa: Double,
    val temp_avg: Double,
    val temp_max: Double,
    val temp_min: Double,
    val wind_dir: String,
    val wind_gust: Double,
    val wind_speed: Double
):Parcelable