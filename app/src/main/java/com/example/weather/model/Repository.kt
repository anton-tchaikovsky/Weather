package com.example.weather.model

fun interface RepositoryWeather {
    fun getWeather(lat:Double, lon:Double): Weather
}

fun interface RepositoryListWeather {
    fun getWeatherList(location: Location): List<Weather>
}

fun interface RepositoryCityList {
    fun getCityList(location: Location): List<City>
}