package com.example.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.model.Weather
import com.example.weather.model.city.City
import com.example.weather.repository.RepositoryHistoryImpl

class HistoryWeatherViewModel(private val liveData: MutableLiveData<List<Weather>> = MutableLiveData()):ViewModel() {
    private val repositoryHistory = RepositoryHistoryImpl()

    fun getLiveData() = liveData

    fun getHistoryWeather(){
        ThreadHistory().getHandler().post{
            liveData.postValue(repositoryHistory.getAllHistoryWeather())
        }
    }

    fun getHistoryWeatherByCity(city: City){
        ThreadHistory().getHandler().post{
            liveData.postValue(repositoryHistory.getHistoryWeatherByCity(city))
        }
    }

    fun deleteAllHistoryWeather(){
        ThreadHistory().getHandler().post{
            repositoryHistory.deleteAllHistoryWeather()
            liveData.postValue(repositoryHistory.getAllHistoryWeather())
        }
    }

    fun deleteHistoryByCity(city: City){
        ThreadHistory().getHandler().post{
            repositoryHistory.deleteAllHistoryWeatherByCity(city)
            liveData.postValue(repositoryHistory.getHistoryWeatherByCity(city))
        }
    }
}