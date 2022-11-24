package com.example.weather.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.weather.broadcastReceiver.ConnectivityBroadcastReceiver
import com.example.weather.databinding.WeatherActivityBinding
import com.example.weather.utils.CHANNEL_ID
import com.example.weather.utils.CHANNEL_NAME
import com.example.weather.viewmodel.ThemeViewModel

class WeatherActivity : AppCompatActivity() {

    private var _binding:WeatherActivityBinding? = null
    private val binding get() = _binding!!

    // создание broadcast-ресивера для приема и обработки данных о изменении подключения к сети
    private val connectivityBroadcastReceiver = ConnectivityBroadcastReceiver()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = WeatherActivityBinding.inflate(layoutInflater)

        //получаем данные из ThemeViewModel и выставляем соответствующую тему
       setTheme (ViewModelProvider(this@WeatherActivity)[ThemeViewModel::class.java].getTheme())

        // создание view соответствующего макета и его установка
        setContentView(binding.root)

        // создаем NotificationChannel для запуска WeatherActivity при срабатывании BroadcastReceiverForStartApp
        initNotificationChannel()

        // создание и запуск CityListFragment
     if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.container.id, CityListFragment.newInstance())
                .commitNow()
        }
    }

    override fun onStart() {
        // регистрируем connectivityBroadcastReceiver
            @Suppress("DEPRECATION")
            registerReceiver(connectivityBroadcastReceiver, IntentFilter(
                ConnectivityManager.CONNECTIVITY_ACTION))
        super.onStart()
    }

    override fun onStop() {
        unregisterReceiver(connectivityBroadcastReceiver)
        ConnectivityBroadcastReceiver.isFirstReceive=true
        super.onStop()
    }

    private fun initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // создаем notificationManager
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            // создаем канал (без настроек)
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
            // запускаем канал
            notificationManager.createNotificationChannel(channel)
        }

    }

}



