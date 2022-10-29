package com.example.weather.model

class RepositoryLocalImpl:RepositoryCityList,RepositoryWeather {
    override fun getWeather(lat:Double, lon:Double): Weather = Weather()
    override fun getCityList(location: Location): List<City> =
        when(location){
            Location.LocationRus -> getCityListRus()
            Location.LocationWorld -> getCityListWorld()
        }
}