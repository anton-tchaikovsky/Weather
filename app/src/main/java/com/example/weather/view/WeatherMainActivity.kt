package com.example.weather.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.weather.R
import com.example.weather.databinding.WeatherActivityMainBinding

class WeatherMainActivity : AppCompatActivity() {

    // создание переменной binding, относящейся к классу соответствующего макета
    private lateinit var binding: WeatherActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // создание ссылки на view соответствующего макета
        binding= WeatherActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // создание и запуск WeatherListFragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, WeatherListFragment.newInstance())
                .commitNow()
        }
    }
}


