package com.example.weather.model

import com.example.weather.BuildConfig
import com.example.weather.model.dto.WeatherDTO
import com.example.weather.utils.BASE_URL
import com.google.gson.GsonBuilder
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataWeatherSource {

    // создаем запрос
    private val weatherAPI = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
        .build().create(WeatherAPI::class.java)

    // заполняем и отправляем запрос, ставим его в очередь на получение данных
    fun getWeatherFromWebService(city: City, callback: Callback<WeatherDTO>){
        weatherAPI.getWeatherDTO(BuildConfig.WEATHER_API_KEY, city.lat, city.lon).enqueue(callback)
    }
}