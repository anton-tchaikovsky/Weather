package com.example.weather.model.dto

data class Fact(
    val condition: String,
    val temp: Double,
    val feels_like: Double,
    val daytime: String,
    val humidity: Double,
    val icon: String,
    val obs_time: Double,
    val polar: Boolean,
    val pressure_mm: Double,
    val pressure_pa: Double,
    val season: String,
    val wind_dir: String,
    val wind_gust: Double,
    val wind_speed: Double
)