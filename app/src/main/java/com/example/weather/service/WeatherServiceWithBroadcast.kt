package com.example.weather.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
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

class WeatherServiceWithBroadcast : Service() {

    private val broadcastIntent = Intent(WEATHER_INTENT_ACTION)

    @SuppressLint("NewApi")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // получили конкретный город из intent
        intent?.getParcelableExtra<City>(CITY)?.let {
            weatherLoading(it)
        }
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder =
        Binder()

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