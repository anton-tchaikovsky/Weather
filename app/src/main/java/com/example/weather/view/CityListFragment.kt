package com.example.weather.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.databinding.FragmentCityListBinding
import com.example.weather.model.Location
import com.example.weather.model.Weather
import com.example.weather.viewmodel.AppState
import com.example.weather.viewmodel.WeatherListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
        activity?.supportFragmentManager?.apply {
            beginTransaction()
                .add(R.id.container, WeatherListFragment.newInstance(weather))
                .addToBackStack("")
                .commitAllowingStateLoss()}
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
        // создание наблюдателя, обработка исключения, при отсутствии данных
        val observer = Observer<AppState>{
            try {
                renderData(it)
            } catch (e:IllegalStateException){
                createAlertDialogError(e.message.toString())
            }
        }

        viewModel.run {
            //создание ссылки на LiveData и передача liveDataBackground информации о владельце жизненного цикла WeatherListFragment и наблюдателе
            getLiveData().observe(viewLifecycleOwner, observer)
            //первичный запрос во WeatherListViewModel для получения информации о погоде
            loadingListWeather(Location.LocationRus)
        }

        // установка слушателя на FAB
        binding.cityFAB.setOnClickListener {
            isRussian = !isRussian
            if (isRussian){
                (it as FloatingActionButton).setImageDrawable(resources.getDrawable(R.drawable.russia, activity?.theme))
                // запрос во WeatherListViewModel для получения информации о погоде в городах РФ
                viewModel.getWeatherList(Location.LocationRus)
            }
            else{
                (it as FloatingActionButton).setImageDrawable(resources.getDrawable(R.drawable.world, activity?.theme))
                // запрос во WeatherListViewModel для получения информации о погоде в городах мира
                viewModel.getWeatherList(Location.LocationWorld)
            }
        }
    }

    // создание диалогового окна на случай отсутствия подключения к источнику данных
    private fun createAlertDialogError(title: String) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setIcon(R.drawable.ic_baseline_error_24)
            .setCancelable(false)
            .setPositiveButton("Повторить попытку"
            ) { _, _ -> viewModel.loadingListWeather(Location.LocationRus)}
            .setNegativeButton("Выйти из приложения") {_, _ -> activity?.finish() }
            .show()
    }

    // обработка данных о погоде (в том числе о состоянии загрузки данных), полученных от liveData
    private fun renderData(appState: AppState) {
        when (appState){
            AppState.Loading -> {
                binding.showLoading()
            }
            is AppState.Success -> {
                binding.showFAB()
                setCitiesList(appState.weatherList)
            }
            is AppState.Error -> {throw appState.error
            }
        }
    }

    // метод настраивает отображение при иммитации загрузки
    private fun FragmentCityListBinding.showLoading() {
        // отклюение видимости FAB
        cityFAB.visibility = View.GONE
        // включение видимости макета c progressBar
        loadingLayout.visibility = View.VISIBLE
    }

    // метод настраивает отображение после иммитации загрузки
    private fun FragmentCityListBinding.showFAB() {
        // включение видимости FAB
        cityFAB.visibility = View.VISIBLE
        // отключение видимости макета c progressBar
        loadingLayout.visibility = View.GONE
    }

    // отрисовка списка городов
    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    private fun setCitiesList(weatherList: List<Weather>) {
        // передача в адаптер weatherList
        citiesListAdapter.setCitiesList(weatherList)
        // подключение адаптера к RecyclerView
        binding.citiesList.adapter = citiesListAdapter
    }

    override fun onDestroy() {
        _binding=null
        citiesListAdapter.removeListener()
        super.onDestroy()
    }
}

