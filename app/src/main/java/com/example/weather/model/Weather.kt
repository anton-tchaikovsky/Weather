package com.example.weather.model

data class Weather(val city: City = getDefaultCity(),
                   val temperature: Int = 15,
                   val feelsLike: Int = 9)
private fun getDefaultCity() = City("Москва", 55.755826, 37.617299900000035)

