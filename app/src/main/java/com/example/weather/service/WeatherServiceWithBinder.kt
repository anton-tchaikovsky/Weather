package com.example.weather.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import com.example.weather.BuildConfig
import com.example.weather.model.City
import com.example.weather.model.dto.WeatherDTO
import com.example.weather.utils.*
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class WeatherServiceWithBinder : Service() {

    private val binderWeather = BinderWeather()

    override fun onBind(intent: Intent): IBinder {
        return binderWeather
    }

    @RequiresApi(Build.VERSION_CODES.N)
   fun weatherLoading(city: City): LoadingState {
        try {
            // создали url для запроса погоды в конкретном городе
            val url = URL(setURL(city))
                // создали пустое соединение https
                var urlConnection: HttpsURLConnection? = null
                try {
                    // открыли соединение и настроили
                    urlConnection = (url.openConnection() as HttpsURLConnection).apply {
                        // установили метод для получения данных
                        requestMethod = REQUEST_METHOD_GET
                        // добавляем свойства запроса
                        addRequestProperty(API_KEY_PROPERTIES, BuildConfig.WEATHER_API_KEY)
                        // установили время ожидания
                        connectTimeout = CONNECT_TIMEOUT
                    }
                    // читаем данные и переводим данные в виде String
                    val result =
                        getLines(BufferedReader(InputStreamReader(urlConnection.inputStream)))
                    // преобразовываем считанные данные из JSON в Weather DTO и возвращаем их
                   return LoadingState.Success(Gson().fromJson(
                        result,
                        WeatherDTO::class.java
                    ))
                } catch (e: Exception) {
                    return LoadingState.Error(e)
                } finally {
                    urlConnection?.disconnect()

                }
        } catch (e: MalformedURLException) {
            return LoadingState.Error(e)
        }
    }

   class BinderWeather: Binder(){
      fun getWeatherServiceWithBinder() = WeatherServiceWithBinder()
    }

}