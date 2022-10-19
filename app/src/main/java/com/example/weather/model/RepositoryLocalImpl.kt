package com.example.weather.model

class RepositoryLocalImpl:Repository {
    override fun getWeather() = Weather()
    override fun getWeatherWorldCities() = getDataWeatherWorldCities()
    override fun getWeatherRussianCities() = getDataWeatherRussianCities()
}