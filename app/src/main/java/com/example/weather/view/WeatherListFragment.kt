package com.example.weather.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.databinding.WeatherFragmentMainBinding
import com.example.weather.model.Weather
import com.example.weather.viewmodel.Seasons
import com.example.weather.viewmodel.WeatherListViewModel

class WeatherListFragment : Fragment() {

    // создание companion object (статического метода) для получения экземпляра WeatherListFragment
    companion object {
        const val BUNDLE_EXTRA = "weather"
        fun newInstance(weather: Weather): WeatherListFragment =
            WeatherListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(BUNDLE_EXTRA, weather)
                }
            }
    }

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
        ViewModelProvider(this@WeatherListFragment)[WeatherListViewModel::class.java].also {WeatherListViewModel ->
            // передача liveDataBackground информации о владельце жизненного цикла WeatherListFragment и наблюдателе (через лямбду)
            WeatherListViewModel.getLiveDataBackground().observe(viewLifecycleOwner) {
                //метод, который реализует наблюдатель при получении данных от LiveDataBackground
                renderBackground(it)
            }
        }.run {
            // запрос во WeatherListViewModel для подучения информации, необходимой для установки фона root макета WeatherListFragment
            getBackground() }

        // получаем погоду для конкретного города (с проверкой на null) и вызываем метод setWeather(weather)
        arguments?.getParcelable<Weather>(BUNDLE_EXTRA)?.let { weather -> setWeather(weather) }
    }

    // отрисовка данных о погоде в конкретном городе
    private fun setWeather(weather: Weather) {
        weather.let {
            with(binding) {
                cityName.text = it.city.cityName
                cityCoordinates.text = String.format(
                    getString(R.string.city_coordinates),
                    it.city.lat,
                    it.city.lon
                )
                temperatureValue.text = it.temperature.toString()
                feelsLikeValue.text = it.feelsLike.toString() }
        }
    }

    // метод устанавливает фон root макета WeatherListFragment на основе данных полученных от liveDataBackground
    private fun renderBackground(season: Seasons) {
        when (season){
            Seasons.Summer ->  binding.setBackgroundDrawable(R.drawable.summer)
            Seasons.Autumn ->  binding.setBackgroundDrawable(R.drawable.autumn)
            Seasons.Spring ->  binding.setBackgroundDrawable(R.drawable.spring)
            Seasons.Winter ->  binding.setBackgroundDrawable(R.drawable.winter)
        }
    }

    // метод устанавливает фон для root по id рисунка
    private fun WeatherFragmentMainBinding.setBackgroundDrawable (id: Int){
        root.background = AppCompatResources.getDrawable(requireContext(),id)
    }

    override fun onDestroyView(){
        _binding=null
        super.onDestroyView()
    }

}


