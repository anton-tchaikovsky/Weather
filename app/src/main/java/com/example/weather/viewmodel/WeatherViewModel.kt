package com.example.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.model.City
import com.example.weather.model.RepositoryRemoteImpl
import com.example.weather.model.dto.WeatherDTO
import com.example.weather.utils.setURL
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException

class WeatherViewModel(private val liveData: MutableLiveData<LoadingState> = MutableLiveData<LoadingState>()): ViewModel() {

    private val repositoryWeather = RepositoryRemoteImpl()

    private val callback = object :Callback{
        override fun onFailure(call: Call, e: IOException) {
           liveData.postValue(LoadingState.Error(e))
        }

        override fun onResponse(call: Call, response: Response) {
            val responseString = response.body()?.string()
            if (response.isSuccessful && responseString !=null)
               liveData.postValue(getWeather(responseString))
            else
                liveData.postValue(LoadingState.Error(IllegalStateException()))
        }
    }

    fun getLiveData() = liveData

    fun getWeatherDTO(city: City){
        repositoryWeather.getWeatherFromServer(setURL(city), callback)
    }

    private fun getWeather(weatherResponse: String?): LoadingState =
    LoadingState.Success(Gson().fromJson(weatherResponse, WeatherDTO::class.java))

}