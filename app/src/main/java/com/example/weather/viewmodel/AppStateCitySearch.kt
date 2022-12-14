package com.example.weather.viewmodel

import com.example.weather.model.city.City

sealed class AppStateCitySearch{
    data class Success (val city: City): AppStateCitySearch()
    data class Error(val error: Throwable) : AppStateCitySearch()
}
