package com.example.weather.model

interface Repository {
    fun getWeather(): Weather
}