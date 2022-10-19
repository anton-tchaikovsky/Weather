package com.example.weather.model

data class Weather(val city: City = getDefaultCity(),
                   val temperature: Int = 15,
                   val feelsLike: Int = 9)
