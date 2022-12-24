package com.example.weather.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.R
import com.example.weather.broadcastReceiver.ConnectivityBroadcastReceiver
import com.example.weather.databinding.WeatherActivityBinding
import com.example.weather.utils.*
import com.google.firebase.messaging.FirebaseMessaging

class WeatherActivity : AppCompatActivity() {

    private var _binding:WeatherActivityBinding? = null
    private val binding get() = _binding!!

    // создание broadcast-ресивера для приема и обработки данных о изменении подключения к сети
    private val connectivityBroadcastReceiver = ConnectivityBroadcastReceiver()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = WeatherActivityBinding.inflate(layoutInflater)

        // создание view соответствующего макета и его установка
        setContentView(binding.root)

        // создаем NotificationChannel
        initNotificationChannel()

        // создание и запуск CityListFragment
     if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.container.id, CityListFragment.newInstance(), TAG_CITY_LIST_FRAGMENT)
                .commitNow()
        }

        // получение токена устройства для Firebase
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful)
                Log.v(TAG_LOG, it.result.toString())
            else
                Log.v(TAG_LOG, TOKEN_ERROR)
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
            // создаем каналы (без настроек)
            //для запуска WeatherActivity при срабатывании BroadcastReceiverForStartApp
            val channelWeather = NotificationChannel(CHANNEL_ID_WEATHER, CHANNEL_NAME_WEATHER, NotificationManager.IMPORTANCE_LOW)
            //при получении уведемлений от Firebase
            val channelFirebase =  NotificationChannel(CHANNEL_ID_FIREBASE, CHANNEL_NAME_FIREBASE, NotificationManager.IMPORTANCE_LOW)
            // запускаем каналы
            notificationManager.run{
                createNotificationChannel(channelWeather)
                createNotificationChannel(channelFirebase)
            }
        }
    }

   override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.history_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if(item.itemId==R.id.contacts_menu){
            supportFragmentManager.run {
                beginTransaction().also {
                    // скрываем CityListFragment, если он виден
                    findFragmentByTag(TAG_CITY_LIST_FRAGMENT)?.run{
                        if (isVisible)
                            it.hide(this)
                    }
                    // скрываем WeatherFragment, если он виден
                    findFragmentByTag(TAG_WEATHER_FRAGMENT)?.run{
                        if (isVisible)
                            it.hide(this)
                    }
                    it.add(R.id.container, ContactsFragment.newInstance(), TAG_CONTACTS_FRAGMENT)
                    .addToBackStack("")
                    .commitAllowingStateLoss()
                }
                true
            }
        } else
        return super.onOptionsItemSelected(item)
    }
}