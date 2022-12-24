package com.example.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.model.Weather
import com.example.weather.model.city.City
import com.example.weather.model.dto.WeatherDTO
import com.example.weather.repository.RepositoryHistoryImpl
import com.example.weather.repository.RepositoryWeatherImpl

class WeatherViewModel(private val liveData: MutableLiveData<AppStateWeather> = MutableLiveData<AppStateWeather>()): ViewModel() {

    private val repositoryWeather = RepositoryWeatherImpl()
    private val repositoryHistory = RepositoryHistoryImpl()

    private val callback = object : retrofit2.Callback<WeatherDTO>{
        override fun onResponse(
            call: retrofit2.Call<WeatherDTO>,
            response: retrofit2.Response<WeatherDTO>
        ) {
            if (response.isSuccessful && response.body() !=null)
                liveData.postValue(AppStateWeather.Success(response.body() as WeatherDTO))
            else
                liveData.postValue(AppStateWeather.Error(IllegalStateException()))
        }

        override fun onFailure(call: retrofit2.Call<WeatherDTO>, t: Throwable) {
            liveData.postValue(AppStateWeather.Error(t))
        }

    }

    fun getLiveData() = liveData

    fun getWeatherDTO(city: City){
        liveData.value = AppStateWeather.Loading
        repositoryWeather.getWeatherFromServer(city, callback)
    }

    fun saveWeather(city: City, weatherDTO: WeatherDTO){
        ThreadHistory().getHandler().post {
            repositoryHistory.saveWeather(Weather(city.cityName, weatherDTO.fact.temp, weatherDTO.fact.condition))
        }
    }
}