package com.example.weather.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.weather.BuildConfig
import com.example.weather.model.dto.WeatherDTO

import com.example.weather.utils.getLines
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL

import javax.net.ssl.HttpsURLConnection

class WeatherLoading (private val lat: Double, private val lon: Double) {

  @RequiresApi(Build.VERSION_CODES.N)
    fun loadWeather(blockOnLoaded: (WeatherDTO) -> Unit, blockOnFailed: (Throwable) -> Unit){
        // создали объект URL
        try{
            val url = URL("https://api.weather.yandex.ru/v2/informers?lat=${lat}&lon=${lon}&[lang=<ru_RU>]")
            // создали поток для запроса
            Thread{
                // создали пустое соединение https
                var urlConnection: HttpsURLConnection? = null
                try {
                    // открыли соединение и настроили
                    urlConnection = (url.openConnection() as HttpsURLConnection).apply {
                        // установили метод для получения данных
                        requestMethod = "GET"
                        // добавляем свойства запроса
                        addRequestProperty("X-Yandex-API-Key", BuildConfig.WEATHER_API_KEY)
                        // установили время ожидания
                        connectTimeout = 10000
                    }
                    // читаем данные и переводим данные в виде String
                    val result = getLines(BufferedReader(InputStreamReader(urlConnection.inputStream)))
                    // преобразовываем считанные данные из JSON в Weather DTO
                    val weatherDTO: WeatherDTO = Gson().fromJson(result,
                        WeatherDTO::class.java)
                    // работаем с полученными данными
                    blockOnLoaded(weatherDTO)
                } catch (e: Exception){
                    blockOnFailed(e)
                } finally {
                    urlConnection?.disconnect()
                }
            }.start()
        } catch (e: MalformedURLException){
            blockOnFailed(e)
        }
    }
}
