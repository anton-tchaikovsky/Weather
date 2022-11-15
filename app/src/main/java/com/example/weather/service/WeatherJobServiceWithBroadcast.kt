package com.example.weather.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
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

class WeatherJobServiceWithBroadcast: JobService() {

    private val broadcastIntent = Intent(WEATHER_INTENT_ACTION)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStartJob(params: JobParameters?): Boolean {
        params?.extras?.let {
            weatherLoading(City("", lat = it.getDouble(LATITUDE), lon = it.getDouble(LONGITUDE)))
        }
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean = true

    @RequiresApi(Build.VERSION_CODES.N)
    private fun weatherLoading(city: City) {
        try {
            // создали url для запроса погоды в конкретном городе
            val url = URL(setURL(city))
            Thread {
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
                    // преобразовываем считанные данные из JSON в Weather DTO
                    val weatherDTO: WeatherDTO = Gson().fromJson(
                        result,
                        WeatherDTO::class.java
                    )
                    // передаем данные
                    successResponse(weatherDTO)

                } catch (e: Exception) {
                    errorResponse(e)
                } finally {
                    urlConnection?.disconnect()
                    // закрываем сервис
                    stopSelf()
                }
            }.start()
        } catch (e: MalformedURLException) {
            errorResponse(e)
        }
    }

    // метод рассылает Broadcast-сообщение с данными о погоде
    private fun successResponse(weatherDTO: WeatherDTO) {
        LocalBroadcastManager.getInstance(this)
            .sendBroadcast(broadcastIntent.apply {
                putExtra(RESULT_LOADING, SUCCESS_LOADING)
                putExtra(WEATHER_DTO, weatherDTO)
            })
    }

    // метод рассылает Broadcast-сообщение с ошибкой, возникающей при загрузке данных о погоде
    private fun errorResponse(e: Throwable) {
        LocalBroadcastManager.getInstance(this)
            .sendBroadcast(broadcastIntent.apply {
                putExtra(RESULT_LOADING, ERROR_LOADING)
                putExtra(ERROR, e)
            })
    }
}