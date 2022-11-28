package com.example.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.model.Weather
import com.example.weather.repository.RepositoryHistoryImpl

class HistoryWeatherViewModel(private val liveData: MutableLiveData<List<Weather>> = MutableLiveData()):ViewModel() {
    private val repositoryHistory = RepositoryHistoryImpl()

    fun getLiveData() = liveData

    fun getHistoryWeather(){
        liveData.value = repositoryHistory.getAllHistoryWeather()
    }
}