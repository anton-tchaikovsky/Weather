package com.example.weather.model

interface Repository {
    fun getWeatherFromServer(): Weather
    fun getWeatherFromLocalStorage(): Weather
}