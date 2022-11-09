package com.example.weather.view

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.weather.databinding.WeatherActivityBinding
import com.example.weather.service.FibonacciService
import com.example.weather.viewmodel.ThemeViewModel

class WeatherActivity : AppCompatActivity() {

    private var _binding:WeatherActivityBinding? = null
    private val binding get() = _binding!!

    private var isBound = false
    private lateinit var serviceBinder:FibonacciService
    private lateinit var serviceConnection: ServiceConnection

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = WeatherActivityBinding.inflate(layoutInflater)

        //получаем данные из ThemeViewModel и выставляем соответствующую тему
       setTheme (ViewModelProvider(this@WeatherActivity)[ThemeViewModel::class.java].getTheme())

        // создание view соответствующего макета и его установка
        setContentView(binding.root)

        // создание и запуск CityListFragment
       /*if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.container.id, CityListFragment.newInstance())
                .commitNow()
        }*/

        binding.buttonBindService.setOnClickListener {

            serviceConnection = object : ServiceConnection{
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    isBound = true
                    serviceBinder = (service as FibonacciService.ServiceBinder).getService()
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    isBound = false
                }

            }

            bindService(Intent(this@WeatherActivity,FibonacciService::class.java), serviceConnection, BIND_AUTO_CREATE)
        }

        binding.buttonNextFibo.setOnClickListener {
            binding.textFibonacci.apply {
                text = if (isBound)
                    serviceBinder.getNextFibonacci().toString()
                else
                    "Нет соединения с сервисом"
            }
        }
    }

    override fun onStop() {
        unbindService(serviceConnection)
        super.onStop()
    }
}



