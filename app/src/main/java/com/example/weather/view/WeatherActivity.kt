package com.example.weather.view

import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.databinding.WeatherActivityBinding
import com.example.weather.service.ThreadFragment
import com.example.weather.viewmodel.ThemeViewModel

class WeatherActivity : AppCompatActivity() {

    private var _binding:WeatherActivityBinding? = null
    private val binding get() = _binding!!

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==R.id.menu_thread){
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ThreadFragment.newInstance())
                .addToBackStack("")
                .commit()
        }
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = WeatherActivityBinding.inflate(layoutInflater)

        //получаем данные из ThemeViewModel и выставляем соответствующую тему
       setTheme (ViewModelProvider(this@WeatherActivity)[ThemeViewModel::class.java].getTheme())

        // создание view соответствующего макета и его установка
        setContentView(binding.root)

        // создание и запуск CityListFragment
       if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.container.id, CityListFragment.newInstance())
                .commitNow()
        }
    }

}



