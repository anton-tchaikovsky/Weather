package com.example.weather.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.FragmentCityListBinding
import com.example.weather.model.Location
import com.example.weather.model.Weather
import com.example.weather.viewmodel.AppState
import com.example.weather.viewmodel.WeatherListViewModel

class CityListFragment : Fragment() {


    private var _binding: FragmentCityListBinding?=null
    private val binding get() = _binding!!

    // функциональный интерфейс для обработки нажатия на элемент
    fun interface OnItemCityClickListener{
        fun onItemClick(weather: Weather)
    }

    // создаем адаптер CityListFragmentAdapter и передаем в конструктор объект OnItemCityClickListener,
    // при этом переопределяем метод onItemClick(Weather) (реализация через лямбду)
    private val citiesListAdapter = CityListFragmentAdapter { weather ->
        val manager = activity?.supportFragmentManager
        manager?.beginTransaction()
            ?.add(R.id.container, WeatherListFragment.newInstance(weather))
            ?.addToBackStack("")?.commitAllowingStateLoss()
    }

    // создаем ссылку на viewModel
    private lateinit var viewModel:WeatherListViewModel

    private var isRussian = true

    companion object {
        fun newInstance() = CityListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCityListBinding.inflate(inflater, container,false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // получение ссылки на WeatherListViewModel, не на прямую, а через ViewModelProvider
        viewModel = ViewModelProvider(this@CityListFragment)[WeatherListViewModel::class.java]
        // создание наблюдателя
        val observer = Observer<AppState>{renderData(it)}
        //создание ссылки на LiveData и передача liveDataBackground информации о владельце жизненного цикла WeatherListFragment и наблюдателе
        viewModel.getLiveData().observe(viewLifecycleOwner, observer)

        //первичный запрос во WeatherListViewModel для получения информации о погоде
        getDataWeatherLoading()

        // установка слушателя на FAB
        binding.cityFAB.setOnClickListener {
            isRussian = !isRussian
            if (isRussian){
                binding.cityFAB.setImageDrawable(resources.getDrawable(R.drawable.russia, null))
                // запрос во WeatherListViewModel для получения информации о погоде в городах РФ
                viewModel.getWeatherList(Location.LocationRus)
            }
            else{
                binding.cityFAB.setImageDrawable(resources.getDrawable(R.drawable.world, null))
                // запрос во WeatherListViewModel для получения информации о погоде в городах мира
                viewModel.getWeatherList(Location.LocationWorld)
            }
        }
    }

    // первичный запрос во WeatherListViewModel для получения информации о погоде (в том числе о состоянии загрузки данных), обработка исключения, вызываемого отсутствием подключения к источнику данных
    private fun getDataWeatherLoading() {
        try {
            viewModel.loadingListWeather(Location.LocationRus)
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
            ) { _, _ -> getDataWeatherLoading()}
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
                val weatherList = appState.weatherList
                // отключение видимости макета c progressBar
                binding.loadingLayout.visibility = View.GONE
                // включение видимости FAB
                binding.cityFAB.visibility = View.VISIBLE

                setCitiesList(weatherList)
            }
            is AppState.Error -> {
            }
        }
    }

    // отрисовка списка городов
    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    private fun setCitiesList(weatherList: List<Weather>) {
        // создание переменной для RecyclerView
        val citiesList:RecyclerView = binding.citiesList
        // передача в адаптер weatherList
        citiesListAdapter.setCitiesList(weatherList)
        citiesListAdapter.notifyDataSetChanged()
        // подключение адаптера к RecyclerView
        citiesList.adapter = citiesListAdapter
    }

    override fun onDestroy() {
        _binding=null
        citiesListAdapter.removeListener()
        super.onDestroy()
    }
}

