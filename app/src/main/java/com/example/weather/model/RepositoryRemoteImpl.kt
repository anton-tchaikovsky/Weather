package com.example.weather.model

class RepositoryRemoteImpl:RepositoryCityList,RepositoryWeather {
    override fun getWeather(lat:Double, lon:Double)= Weather()
    override fun getCityList(location: Location): List<City> =
        when(location){
            Location.LocationRus -> getCityListRus()
            Location.LocationWorld -> getCityListWorld()
        }
}