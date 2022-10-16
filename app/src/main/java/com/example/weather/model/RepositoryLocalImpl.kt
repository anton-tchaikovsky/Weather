package com.example.weather.model

class RepositoryLocalImpl:Repository {
    override fun getWeather(): Weather {
        return Weather()
    }
}