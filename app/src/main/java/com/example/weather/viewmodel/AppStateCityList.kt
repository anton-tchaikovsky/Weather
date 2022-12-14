package com.example.weather.viewmodel

import com.example.weather.model.city.City

sealed class AppStateCityList{
    data class Success (val cityList: List <City>): AppStateCityList()
    data class Error(val error: Throwable) : AppStateCityList()
    object Loading: AppStateCityList()
}
