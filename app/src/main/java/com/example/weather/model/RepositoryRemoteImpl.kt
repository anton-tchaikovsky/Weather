package com.example.weather.model

class RepositoryRemoteImpl:RepositoryListWeather,RepositoryWeather {
    override fun getWeather(lat:Double, lon:Double)= Weather()
    override fun getWeatherList(location: Location): List<Weather> =
        when(location){
            Location.LocationRus -> getWeatherListRus()
            Location.LocationWorld -> getWeatherListWorld()
        }
}