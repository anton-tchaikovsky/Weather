package com.example.weather.model

fun interface RepositoryWeather {
    fun getWeatherFromServer(requestLink:String, callback: okhttp3.Callback)
}

fun interface RepositoryCityList {
    fun getCityList(location: Location): List<City>
}

fun interface RepositoryThemes {
    fun getTheme(themeKey:String): Int
}