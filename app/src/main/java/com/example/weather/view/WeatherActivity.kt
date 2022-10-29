package com.example.weather.view

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.R
import com.example.weather.databinding.WeatherActivityBinding


class WeatherActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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



