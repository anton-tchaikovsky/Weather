package com.example.weather.model

class RepositoryImpl:Repository {
    override fun getWeatherFromServer(): Weather {
        return Weather()
    }

    override fun getWeatherFromLocalStorage(): Weather {
        return Weather(temperature = 15, feelsLike = 9)
    }
}