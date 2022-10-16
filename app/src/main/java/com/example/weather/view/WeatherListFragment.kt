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
       fun newInstance() = WeatherListFragment()
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
        // создание ссылки на LiveData из WeatherListViewModel
        val liveDataToObserve = viewModel.getLiveData()
        // создание наблюдателя
        val observer = Observer<AppState> {
            renderData(it) //метод, который реализует наблюдатель при получении данных от LiveData
        }
        // передача liveData информации о владельце жизненным циклом WeatherListFragment и наблюдателе
        liveDataToObserve.observe(viewLifecycleOwner, observer)

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

        // запрос во WeatherListViewModel для подучения информации о погоде
        // (через отдельный метод для обработки исключения, вызываемого отсутствием подключения к источнику данных)
        getDataWeather()

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

    // запрос во WeatherListViewModel для подучения информации о погоде (в том числе о состоянии загрузки данных), обработка исключения, вызываемого отсутствием подключения к источнику данных
    private fun getDataWeather() {
        try {
            viewModel.getDataWeather()
        } catch (e: IllegalStateException) {
            createAlertDialogError(e.message.toString())
        }
    }

    // создание диалогового окна на случай отсутствия подключения к источнику данных
    private fun createAlertDialogError(title: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setIcon(R.drawable.ic_baseline_error_24)
            .setCancelable(false)
            .setPositiveButton("Повторить попытку"
            ) { _, _ -> getDataWeather()}
            .setNegativeButton("Выйти из приложения") {_, _ -> activity?.finish() }
            .show()
    }

    // обработка данных о погоде (в том числе о состоянии загрузки данных), полученных от liveData
    private fun renderData(appState: AppState) {

            when (appState){
                AppState.Loading -> {
                    // включение видимости макета c progressBar
                    binding.loadingLayout.visibility = View.VISIBLE
                }
                is AppState.Success -> {
                    val weatherData = appState.weatherData
                    // отключение видимости макета c progressBar
                    binding.loadingLayout.visibility = View.GONE
                    // отключение видимости макета c данными о погоде
                    binding.mainView.visibility = View.VISIBLE
                    // заполнение макета данными о погоде
                    setData(weatherData)
                }
                is AppState.Error -> {
                }
            }

        }

    // метод заполняет данными о погоде соответствующие view
    private fun setData (weatherData: Weather){
        binding.cityName.text = weatherData.city.cityName
        binding.cityCoordinates.text = String.format(
            getString(R.string.city_coordinates),
            weatherData.city.lat,
            weatherData.city.lon
        )
        binding.temperatureValue.text = weatherData.temperature.toString()
        binding.feelsLikeValue.text = weatherData.feelsLike.toString()

    }

    override fun onDestroyView(){
        _binding=null
        super.onDestroyView()
    }


}


