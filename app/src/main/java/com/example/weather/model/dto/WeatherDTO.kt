package com.example.weather.model.dto

data class WeatherDTO(
    val fact: Fact,
    val forecast: Forecast,
    val info: Info,
    val now: Double,
    val now_dt: String
)