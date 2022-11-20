package com.example.weather.viewmodel

import com.example.weather.model.dto.WeatherDTO

sealed class LoadingState{
    data class Success(val weatherDTO: WeatherDTO): LoadingState()
    data class Error(val error: Throwable): LoadingState()
}
