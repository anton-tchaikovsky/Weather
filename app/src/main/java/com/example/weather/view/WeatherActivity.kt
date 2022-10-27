package com.example.weather.view

import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.databinding.WebViewBinding
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection


class WeatherActivity : AppCompatActivity() {

    private lateinit var binding:WebViewBinding

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val clickListener: View.OnClickListener = View.OnClickListener {

            try {
                // создали объект URL
                val url = URL(binding.url.text.toString())
                // запоминаем основной поток
                val handler = Handler(Looper.getMainLooper())
                Thread {
                    // создали пустое соединение https
                    var urlConnection: HttpsURLConnection? = null
                    try {
                        // открыли соединение
                        urlConnection = url.openConnection() as HttpsURLConnection
                        // установили метод для получения данных
                        urlConnection.requestMethod = "GET"
                        // установили время ожидания
                        urlConnection.readTimeout = 10000
                        // читаем данные
                        val reader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                        // данные в виде String
                        val result = getLines(reader)

                        // возвращаемся к основному потоку
                        handler.post {
                            // загружаем данные в webView для отображения
                            binding.webView.loadData(
                            result, "text/html; charset=utf-8",
                            "utf-8")  }

                    } catch (e: Exception) {
                        Log.e("MyTAG", "Fail connection", e)
                        e.printStackTrace()
                    } finally {
                        // закрыли соединение
                        urlConnection?.disconnect()
                    }
                }.start()
            } catch (e: MalformedURLException) {
                Log.e("", "Fail URI", e)
                e.printStackTrace()
            }

        }

        binding.ok.setOnClickListener(clickListener)

/*
        // создание view соответствующего макета и его установка
        setContentView(WeatherActivityBinding.inflate(layoutInflater).root)

        // создание и запуск CityListFragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, CityListFragment.newInstance())
                .commitNow()
        }
*/
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getLines(reader: BufferedReader): String {
        return reader.lines().collect(Collectors.joining("\n"))
    }
}


