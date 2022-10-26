package com.example.weather.model

class RepositoryLocalImpl:RepositoryListWeather,RepositoryWeather {
    override fun getWeather(lat:Double, lon:Double): Weather = Weather()
    override fun getWeatherList(location: Location): List<Weather> =
        when(location){
            Location.LocationRus -> getWeatherListRus()
            Location.LocationWorld -> getWeatherListWorld()
        }
}