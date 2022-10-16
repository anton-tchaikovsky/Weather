package com.example.weather.model

class RepositoryRemoteImpl:Repository {
    override fun getWeather(): Weather {
        return Weather()
    }
}