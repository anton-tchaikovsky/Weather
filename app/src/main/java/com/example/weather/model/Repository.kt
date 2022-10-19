package com.example.weather.model

interface Repository {
    fun getWeather(): Weather
    fun getWeatherWorldCities(): List<Weather>
    fun getWeatherRussianCities(): List<Weather>
}