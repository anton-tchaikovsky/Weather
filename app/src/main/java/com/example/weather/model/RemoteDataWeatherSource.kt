package com.example.weather.model

import com.example.weather.BuildConfig
import com.example.weather.utils.API_KEY_PROPERTIES
import okhttp3.OkHttpClient
import okhttp3.Request

class RemoteDataWeatherSource {

    fun getWeatherFromWebService(requestLink: String, callback: okhttp3.Callback){
        // настраиваем и создаем запрос
        val request = Request.Builder()
            .header(API_KEY_PROPERTIES, BuildConfig.WEATHER_API_KEY)
            .url(requestLink)
            .build()
        // отправляем запрос и ставим его в очередь на получение ответа
        OkHttpClient().newCall(request).enqueue(callback)
    }
}