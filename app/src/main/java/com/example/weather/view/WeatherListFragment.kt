package com.example.weather.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.databinding.WeatherFragmentMainBinding
import com.example.weather.model.City
import com.example.weather.model.dto.WeatherDTO
import com.example.weather.utils.translateConditionInRussian
import com.example.weather.viewmodel.Seasons
import com.example.weather.viewmodel.WeatherListViewModel
import com.example.weather.viewmodel.WeatherLoading
import java.net.UnknownHostException

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
        WeatherLoading(city.lat, city.lon).loadWeather(::onLoaded, ::onFailed)
    }

    // отрисовка данных о погоде в конкретном городе
    private fun setWeatherForView(city: City, weatherDTO: (WeatherDTO)) {
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
                weatherDTO.fact.let {
                    temperatureValue.text = it.temp.toString()
                    feelsLikeValue.text = it.feels_like.toString()
                    weatherCondition.text = translateConditionInRussian(it.condition)
                }
                // видимость label
                temperatureLabel.visibility = View.VISIBLE
                feelsLikeLabel.visibility = View.VISIBLE
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

    // создание диалогового окна на случай отсутствия подключения к интернету
    private fun createAlertDialogForNoNetworkConnection() {
        AlertDialog.Builder(requireContext())
            .setTitle("Нет подключения к сети")
            .setIcon(R.drawable.ic_baseline_error_24)
            .setMessage("Подключитесь к сети и повторите запрос")
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok)
             { _, _ -> requireActivity().supportFragmentManager.popBackStack()}
            .setNegativeButton("Выйти из приложения") {_, _ -> activity?.finish() }
            .show()
    }

    // создание диалогового окна на случай ошибок получения данных из интернета, требующих внесения программных изменений в запрос
    private fun createAlertDialogForNoOtherErrors() {
        AlertDialog.Builder(requireContext())
            .setTitle("Ошибка доступа к данным погоды")
            .setIcon(R.drawable.ic_baseline_error_24)
            .setMessage("Приложение будет закрыто")
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok)
            { _, _ -> activity?.finish() }
            .show()
    }

    private fun onLoaded(weatherDTO: WeatherDTO) {
        requireActivity().runOnUiThread {
            setWeatherForView(city, weatherDTO)
        }
}

   private fun onFailed(throwable: Throwable) {
       requireActivity().runOnUiThread{
           if(throwable is UnknownHostException)
               createAlertDialogForNoNetworkConnection()
           else
               createAlertDialogForNoOtherErrors()
       }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}


