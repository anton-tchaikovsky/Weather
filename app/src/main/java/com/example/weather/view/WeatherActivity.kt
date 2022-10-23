package com.example.weather.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weather.R
import com.example.weather.databinding.WeatherActivityBinding


class WeatherActivity : AppCompatActivity() {

    // создание переменной binding, относящейся к классу соответствующего макета
    private lateinit var binding: WeatherActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // создание ссылки на view соответствующего макета
        binding= WeatherActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // создание и запуск CityListFragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, CityListFragment.newInstance())
                .commitNow()
        }
    }

}


