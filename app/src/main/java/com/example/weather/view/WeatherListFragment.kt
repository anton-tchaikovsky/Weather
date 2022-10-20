package com.example.weather.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.databinding.WeatherFragmentMainBinding
import com.example.weather.viewmodel.Seasons
import com.example.weather.model.Weather
import com.example.weather.viewmodel.AppState
import com.example.weather.viewmodel.WeatherListViewModel

class WeatherListFragment : Fragment() {

    // создание companion object (статического метода) для получения экземпляра WeatherListFragment
    companion object {
        const val BUNDLE_EXTRA = "weather"
        fun newInstance(weather: Weather):WeatherListFragment{
           val weatherListFragment = WeatherListFragment()
           val bundle = Bundle()
            bundle.putParcelable(BUNDLE_EXTRA, weather)
            weatherListFragment.arguments = bundle
           return weatherListFragment
       }
    }

    // создание переменной для доступа к WeatherListViewModel
    private lateinit var viewModel: WeatherListViewModel


    // создание переменной binding, относящейся к классу соответствующего макета
    private var _binding: WeatherFragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // создание ссылки на view соответствующего макета
        _binding = WeatherFragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // получение ссылки на WeatherListViewModel, не на прямую, а через ViewModelProvider
        viewModel = ViewModelProvider(this)[WeatherListViewModel::class.java]

        // создание ссылки на LiveDataBackground из WeatherListViewModel
        val liveDataBackground = viewModel.getLiveDataBackground()
        // создание наблюдателя
        val observerBackground = Observer<Seasons> {
            renderBackground(it) //метод, который реализует наблюдатель при получении данных от LiveDataBackground
        }
        // передача liveData информации о владельце жизненным циклом WeatherListFragment и наблюдателе
        liveDataBackground.observe(viewLifecycleOwner, observerBackground)

        // запрос во WeatherListViewModel для подучения информации, необходимой для установки фона root макета WeatherListFragment
        viewModel.getBackground()

        val weather: Weather?= arguments?.getParcelable<Weather?>(BUNDLE_EXTRA)

        if(weather!=null)
           setWeather(weather)

    }

    private fun setWeather(weather: Weather) {
        binding.cityName.text = weather.city.cityName
        binding.cityCoordinates.text = String.format(
            getString(R.string.city_coordinates),
            weather.city.lat,
            weather.city.lon
        )
        binding.temperatureValue.text = weather.temperature.toString()
        binding.feelsLikeValue.text = weather.feelsLike.toString()
    }

    // метод устанавливает фон root макета WeatherListFragment на основе данных полученных от liveDataBackground
    private fun renderBackground(season: Seasons) {
        when (season){
            Seasons.Summer ->  binding.root.background = AppCompatResources.getDrawable(requireContext(),R.drawable.summer)
            Seasons.Autumn ->  binding.root.background = AppCompatResources.getDrawable(requireContext(),R.drawable.autumn)
            Seasons.Spring ->  binding.root.background = AppCompatResources.getDrawable(requireContext(),R.drawable.spring)
            Seasons.Winter ->  binding.root.background = AppCompatResources.getDrawable(requireContext(),R.drawable.winter)
        }
    }

    override fun onDestroyView(){
        _binding=null
        super.onDestroyView()
    }

}


