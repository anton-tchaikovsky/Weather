package com.example.weather.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.weather.databinding.WebViewBinding


class WeatherActivity : AppCompatActivity() {

    private lateinit var binding:WebViewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WebViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ok.setOnClickListener{
            startActivity(Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(binding.url.text.toString())
             })
        }

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

}


