package com.example.weather.view

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.R
import com.example.weather.databinding.CityListFragmentBinding
import com.example.weather.model.city.City
import com.example.weather.model.city.Location
import com.example.weather.utils.*
import com.example.weather.viewmodel.AppStateCityList
import com.example.weather.viewmodel.AppStateCitySearch
import com.example.weather.viewmodel.CityListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.io.IOException

class CityListFragment : Fragment() {

    private var _binding: CityListFragmentBinding?=null
    private val binding get() = _binding!!

    // функциональный интерфейс для обработки нажатия на элемент
    fun interface OnItemCityClickListener{
        fun onItemClick(city: City)
    }

    // создаем адаптер CityListFragmentAdapter и передаем в конструктор объект OnItemCityClickListener,
    // при этом переопределяем метод onItemClick(Weather) (реализация через лямбду)
    private val citiesListAdapter = CityListAdapter { city ->
        showWeather(city)
    }

    // создаем ссылку на viewModel
    private lateinit var viewModel:CityListViewModel

    private var isRussian = true

    companion object {
        fun newInstance() = CityListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //включаем меню
        setHasOptionsMenu(true)
        _binding = CityListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // подключение адаптера к RecyclerView
        binding.citiesList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = citiesListAdapter
        }

        //запрос в настройки для получения информации о сохраненной локации
        isRussian = activity?.getPreferences(MODE_PRIVATE)?.getBoolean(IS_RUSSIAN, true) ?: true

        // получение ссылки на CityListViewModel, не на прямую, а через ViewModelProvider
        viewModel = ViewModelProvider(this@CityListFragment)[CityListViewModel::class.java]

        // создание наблюдателя, обработка исключения, при отсутствии данных
        val observer = Observer<AppStateCityList>{
            try {
                renderData(it)
            } catch (e:IllegalStateException){
                binding.run {
                    // скрываем FAB
                    cityFAB.visibility = View.GONE
                    searchFAB.visibility = View.GONE
                    // отображаем snackbar с ошибкой загрузки данных
                    root.showSnackbar(e.message.toString(), R.string.return_loading){
                        viewModel.getCityListIf(isRussian)
                    }
                }

            }
        }

        viewModel.run {
            //создание ссылки на LiveDataCityList() и передача информации о владельце жизненного цикла CityListFragment и наблюдателе
            getLiveDataCityList().observe(viewLifecycleOwner, observer)
            //первичный запрос в CityListViewModel для получения информации о списке городов
            loadingCityList(if (isRussian)
                Location.LocationRus
            else
                Location.LocationWorld)
            //создание ссылки на LiveDataCitySearch и передача информации о владельце жизненного цикла CityListFragment и наблюдателе
            getLiveDataCitySearch().observe(viewLifecycleOwner){
                renderData(it)
            }
        }

        // установка слушателя на cityFAB
        binding.cityFAB.setOnClickListener {cityFAB ->
            isRussian = !isRussian
            (cityFAB as FloatingActionButton).setImageDrawableIf(isRussian)
            viewModel.getCityListIf(isRussian)
            //сохраняем в настройки выбранную локацию
            activity?.run {
                getPreferences(MODE_PRIVATE).edit().putBoolean(IS_RUSSIAN, isRussian).apply()
            }
        }

        // установка слушателя на searchFAB
        binding.searchFAB.setOnClickListener{
            // создаем диалог-фрагмент для ввода названия города
            CitySearchDialogFragment.newInstance().show(requireActivity().supportFragmentManager, " CitySearchDialogFragment")
            // получаем результат из диалог-фрагмента
            requireActivity().supportFragmentManager.setFragmentResultListener(KEY_FOR_CITY_NAME_SEARCH, viewLifecycleOwner
            ) { _, result ->
                result.getString(CITY_NAME_SEARCH)?.let{
                    viewModel.searchCity(it)
                }
            }
        }
    }

    // метод создает фрагмент с погодой в городе
    private fun showWeather(city:City){
        activity?.supportFragmentManager?.apply {
            beginTransaction()
                .hide(this@CityListFragment) // скрываем текущий фрагмент(при popBackStake вернется)
                .add(R.id.container, WeatherFragment.newInstance(city), TAG_WEATHER_FRAGMENT)
                .addToBackStack("")
                .commitAllowingStateLoss()}
    }

    // метод устанавливает изображение на FAB в зависимости от условия isRussian
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun FloatingActionButton.setImageDrawableIf (isRussian: Boolean){
        if (isRussian)
            setImageDrawable(resources.getDrawable(R.drawable.russia, activity?.theme))
        else
            setImageDrawable(resources.getDrawable(R.drawable.world, activity?.theme))
    }

    // метод запрашивает список городов в зависимости от условия isRussian
    private fun CityListViewModel.getCityListIf (isRussian: Boolean){
        if (isRussian)
            getCityList(Location.LocationRus)
        else
            getCityList(Location.LocationWorld)
    }

    // создание Snackbar на случай отсутствия подключения к источнику данных
    private fun View.showSnackbar (message: String, actionTextId: Int, duration:Int = Snackbar.LENGTH_INDEFINITE, action: (View) -> Unit) {
        Snackbar.make(this, message, duration ).setAction(getString(actionTextId),action ).show()
    }

    // обработка данных о погоде (в том числе о состоянии загрузки данных), полученных от liveData
    private fun renderData(appStateCityList: AppStateCityList) {
        when (appStateCityList){
            AppStateCityList.Loading -> {
                binding.showLoading()
            }
            is AppStateCityList.Success -> {
                binding.showFAB()
                setCitiesList(appStateCityList.cityList)
            }
            is AppStateCityList.Error -> {throw appStateCityList.error
            }
        }
    }

    // обработка данных о найденом городе, полученных от liveData
    private fun renderData(appStateCitySearch: AppStateCitySearch) {
        when (appStateCitySearch){
            is AppStateCitySearch.Error -> onFailed(appStateCitySearch.error)
            is AppStateCitySearch.Success -> showWeather(appStateCitySearch.city)
        }
    }

    // метод определяет источник ошибки и открывает соответствующее диалоговое окно
    private fun onFailed(throwable: Throwable) {
        if ((throwable is IOException) && !isConnect(context)) // отсутствует подключение к интернету
        createDialogForFailed(MESSAGE_DISCONNECT, MESSAGE_MAKE_CONNECT)
        else if (throwable is IndexOutOfBoundsException) // запрашиваемый город не найден
        createDialogForFailed(SEARCH, NO_SEARCH_CITY)
        else throwable.printStackTrace()
    }

    // создание диалогового окна на случай отсутствия подключения к интернету
    private fun createDialogForFailed(title:String, message: String){
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setIcon(R.drawable.ic_baseline_error_24)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok)
            {dialog, _ -> dialog.dismiss()}
            .show()
    }

    // метод настраивает отображение при иммитации загрузки
    private fun CityListFragmentBinding.showLoading() {
        // отключение видимости FAB
        cityFAB.visibility = View.GONE
        searchFAB.visibility = View.GONE
        // включение видимости макета c progressBar
        includeLoadingLayout.loadingLayout.visibility = View.VISIBLE
    }

    // метод настраивает отображение после иммитации загрузки
    private fun CityListFragmentBinding.showFAB() {
        //установка изображения на FAB
        cityFAB.setImageDrawableIf(isRussian)
        // включение видимости FAB
        cityFAB.visibility = View.VISIBLE
        searchFAB.visibility = View.VISIBLE
        // отключение видимости макета c progressBar
        includeLoadingLayout.loadingLayout.visibility = View.GONE
    }

    // отрисовка списка городов
    @SuppressLint("NotifyDataSetChanged")
    private fun setCitiesList(cityList: List<City>) {
        // передача в адаптер cityList и обновление
        citiesListAdapter.setCitiesList(cityList)
        citiesListAdapter.notifyDataSetChanged()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.history_menu) {
            activity?.supportFragmentManager?.apply {
                beginTransaction().let {
                    it.hide(this@CityListFragment)
                    it.add(
                        R.id.container,
                        HistoryWeatherFragment.newInstance(),
                        TAG_HISTORY_WEATHER_FRAGMENT
                    )
                    it.addToBackStack("")
                    it.commitAllowingStateLoss()
                }
            }
            true
        } else
            super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        _binding = null
        citiesListAdapter.removeListener()
        super.onDestroyView()
    }

}

