package com.example.weather.model

fun interface RepositoryWeather {
    fun getWeather(lat:Double, lon:Double): Weather
}

fun interface RepositoryCityList {
    fun getCityList(location: Location): List<City>
}

fun interface RepositoryThemes {
    fun getTheme(themeKey:String): Int
}