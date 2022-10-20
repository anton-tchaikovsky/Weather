package com.example.weather.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.FragmentCityListBinding
import com.example.weather.model.Weather
import com.example.weather.model.getDataWeatherWorldCities
import com.example.weather.viewmodel.AppState
import com.example.weather.viewmodel.WeatherListViewModel

class CityListFragment : Fragment() {


    private var _binding: FragmentCityListBinding?=null
    private val binding get() = _binding!!

    private val citiesListAdapter = CityListFragmentAdapter(object : OnItemCityClickListener{
        override fun onClick(weather: Weather) {
            val manager = activity?.supportFragmentManager

            if (manager!=null){
                manager.beginTransaction()
                    .add(R.id.container, WeatherListFragment.newInstance(weather))
                    .addToBackStack("")
                    .commitAllowingStateLoss()
            }

        }

    })

    private lateinit var viewModel:WeatherListViewModel

    private var isRussian = true

    companion object {
        fun newInstance() = CityListFragment()
    }

    interface OnItemCityClickListener{
        fun onClick(weather: Weather)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCityListBinding.inflate(inflater, container,false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.cityFAB.setOnClickListener {
            isRussian = !isRussian
            if (isRussian)
                viewModel.getDataWeatherRus()
            else
                viewModel.getDataWeatherWorld()
        }

        viewModel = ViewModelProvider(this)[WeatherListViewModel::class.java]
        viewModel.getLiveData().observe(viewLifecycleOwner) { renderData(it) }
        getDataWeather()

        super.onViewCreated(view, savedInstanceState)

    }



    // запрос во WeatherListViewModel для подучения информации о погоде (в том числе о состоянии загрузки данных), обработка исключения, вызываемого отсутствием подключения к источнику данных
    private fun getDataWeather() {
        try {
            viewModel.getDataWeather(isRussian)
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
                setCitiesList(weatherData)
            }
            is AppState.Error -> {
            }
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setCitiesList(weatherData: List<Weather>) {
        val citiesList:RecyclerView = binding.citiesList
        citiesListAdapter.setCitiesList(weatherData)
        citiesListAdapter.notifyDataSetChanged()
        citiesList.adapter = citiesListAdapter
    }

    override fun onDestroy() {
        _binding=null
        citiesListAdapter.removeListener()
        super.onDestroy()
    }
}

