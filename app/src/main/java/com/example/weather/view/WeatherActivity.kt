package com.example.weather.view

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.databinding.WeatherActivityBinding
import com.example.weather.viewmodel.ThemeViewModel


class WeatherActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // получаем данные из ThemeViewModel и выставляем соответствующую тему
        setTheme (ViewModelProvider(this@WeatherActivity)[ThemeViewModel::class.java].getTheme())

        // создание view соответствующего макета и его установка
        setContentView(WeatherActivityBinding.inflate(layoutInflater).root)

        // создание и запуск CityListFragment
       if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, CityListFragment.newInstance())
                .commitNow()
        }
    }
}



