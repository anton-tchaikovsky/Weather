package com.example.weather.viewmodel

import com.example.weather.model.city.City

sealed class AppState{
    data class Success (val cityList: List <City>): AppState()
    data class Error(val error: Throwable) : AppState()
    object Loading: AppState()
}
