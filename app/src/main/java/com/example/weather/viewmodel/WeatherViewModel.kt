package com.example.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.model.city.City
import com.example.weather.repository.RepositoryRemoteImpl
import com.example.weather.model.dto.WeatherDTO

class WeatherViewModel(private val liveData: MutableLiveData<LoadingState> = MutableLiveData<LoadingState>()): ViewModel() {

    private val repositoryWeather = RepositoryRemoteImpl()

    private val callback = object : retrofit2.Callback<WeatherDTO>{
        override fun onResponse(
            call: retrofit2.Call<WeatherDTO>,
            response: retrofit2.Response<WeatherDTO>
        ) {
            if (response.isSuccessful && response.body() !=null)
                liveData.postValue(LoadingState.Success(response.body() as WeatherDTO))
            else
                liveData.postValue(LoadingState.Error(IllegalStateException()))
        }

        override fun onFailure(call: retrofit2.Call<WeatherDTO>, t: Throwable) {
            liveData.postValue(LoadingState.Error(t))
        }

    }

    fun getLiveData() = liveData

    fun getWeatherDTO(city: City){
        repositoryWeather.getWeatherFromServer(city, callback)
    }
}