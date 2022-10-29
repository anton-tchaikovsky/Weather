package com.example.weather.viewmodel

import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.annotation.RequiresApi
import com.example.weather.BuildConfig
import com.example.weather.model.WeatherDTO
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

class WeatherLoading (private val lat: Double, private val lon: Double, private val weatherLoadingListener: WeatherLoaderListener) {

    interface WeatherLoaderListener {
        fun onLoaded(weatherDTO: WeatherDTO)
        fun onFailed(throwable: Throwable)
    }

  @RequiresApi(Build.VERSION_CODES.N)
    fun loadWeather(){
        // создали объект URL
        val url = URL("https://api.weather.yandex.ru/v2/informers?lat=${lat}&lon=${lon}&[lang=<ru_RU>]")

        // запоминаем основной поток
        val handler = Handler(Looper.getMainLooper())
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
                val weatherDTO:WeatherDTO = Gson().fromJson(result,
                    WeatherDTO::class.java)
                handler.post {
                    // работаем с полученными данными
                    weatherLoadingListener.onLoaded(weatherDTO)
                }
            } catch (e: Exception){
                handler.post{weatherLoadingListener.onFailed(e)}
            } finally {
                urlConnection?.disconnect()
            }
        }.start()

    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }
}
