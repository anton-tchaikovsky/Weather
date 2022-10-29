package com.example.weather.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.databinding.WeatherFragmentMainBinding
import com.example.weather.model.City
import com.example.weather.model.WeatherDTO
import com.example.weather.viewmodel.Seasons
import com.example.weather.viewmodel.WeatherListViewModel
import com.example.weather.viewmodel.WeatherLoading

class WeatherListFragment : Fragment() {

    // создание companion object (статического метода) для получения экземпляра WeatherListFragment
    companion object {
        const val BUNDLE_EXTRA = "weather"
        fun newInstance(city: City): WeatherListFragment =
            WeatherListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BUNDLE_EXTRA, city)
                }
            }
    }

    // создание переменной для города
    private lateinit var city: City

    // создание переменной binding, относящейся к классу соответствующего макета
    private var _binding: WeatherFragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // создание view соответствующего макета
        _binding = WeatherFragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // получение и настройка WeatherListViewModel, не на прямую, а через ViewModelProvider
        ViewModelProvider(this@WeatherListFragment)[WeatherListViewModel::class.java].also { WeatherListViewModel ->
            // передача liveDataBackground информации о владельце жизненного цикла WeatherListFragment и наблюдателе (через лямбду)
            WeatherListViewModel.getLiveDataBackground().observe(viewLifecycleOwner) {
                //метод, который реализует наблюдатель при получении данных от LiveDataBackground
                renderBackground(it)
            }
        }.run {
            // запрос во WeatherListViewModel для подучения информации, необходимой для установки фона root макета WeatherListFragment
            getBackground()
        }

        // получаем конкретный город (с проверкой на null)
        arguments?.getParcelable<City>(BUNDLE_EXTRA)?.let {
            city = it
        }
        // создаем объект для запроса погоды в городе и запрашиваем погоду
        WeatherLoading(city.lat, city.lon, object : WeatherLoading.WeatherLoaderListener {
            override fun onLoaded(weatherDTO: WeatherDTO) {
                setWeather(city, weatherDTO)
            }
            override fun onFailed(throwable: Throwable) {
                Toast.makeText(requireContext(), throwable.message.toString(), Toast.LENGTH_LONG).show()
            }
        }).loadWeather()


    }

    // отрисовка данных о погоде в конкретном городе
    private fun setWeather(city: City, weatherDTO: (WeatherDTO)) {
        with(binding) {
            // отрисовываем данные о конкретном городе
            city.let {
                cityName.text = it.cityName
                cityCoordinates.text = String.format(
                    getString(R.string.city_coordinates),
                    it.lat,
                    it.lon
                )
            }
            // отрисовываем данные о погоде в городе
            weatherDTO.fact?.let {
                temperatureValue.text = it.temp.toString()
                feelsLikeValue.text = it.feels_like.toString()
                weatherCondition.text = it.condition.toString()
            }

        }

    }

    // метод устанавливает фон root макета WeatherListFragment на основе данных полученных от liveDataBackground
    private fun renderBackground(season: Seasons) {
        when (season){
            Seasons.Summer ->  binding.setBackgroundDrawable(R.drawable.summer)
            Seasons.Autumn ->  binding.setBackgroundDrawable(R.drawable.autumn)
            Seasons.Spring -> binding.setBackgroundDrawable(R.drawable.spring)
            Seasons.Winter -> binding.setBackgroundDrawable(R.drawable.winter)
        }
    }

    // метод устанавливает фон для root по id рисунка
    private fun WeatherFragmentMainBinding.setBackgroundDrawable(id: Int) {
        root.background = AppCompatResources.getDrawable(requireContext(), id)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

}


