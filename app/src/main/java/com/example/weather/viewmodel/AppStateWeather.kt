package com.example.weather.viewmodel

import com.example.weather.model.dto.WeatherDTO

sealed class AppStateWeather{
    data class Success(val weatherDTO: WeatherDTO): AppStateWeather()
    data class Error(val error: Throwable): AppStateWeather()
    object Loading: AppStateWeather()
}
