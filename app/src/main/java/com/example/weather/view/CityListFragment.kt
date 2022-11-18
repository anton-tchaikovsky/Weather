package com.example.weather.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.R
import com.example.weather.databinding.FragmentCityListBinding
import com.example.weather.model.City
import com.example.weather.model.Location
import com.example.weather.viewmodel.AppState
import com.example.weather.viewmodel.WeatherListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class CityListFragment : Fragment() {

    private var _binding: FragmentCityListBinding?=null
    private val binding get() = _binding!!

    // функциональный интерфейс для обработки нажатия на элемент
    fun interface OnItemCityClickListener{
        fun onItemClick(city: City)
    }

    // создаем адаптер CityListFragmentAdapter и передаем в конструктор объект OnItemCityClickListener,
    // при этом переопределяем метод onItemClick(Weather) (реализация через лямбду)
    private val citiesListAdapter = CityListFragmentAdapter { city ->
        activity?.supportFragmentManager?.apply {
            beginTransaction()
                .hide(this@CityListFragment) // скрываем текущий фрагмент(при popBackStake вернется)
                .add(R.id.container, WeatherListFragment.newInstance(city))
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
        // подключение адаптера к RecyclerView
        binding.citiesList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = citiesListAdapter
        }

        // получение ссылки на WeatherListViewModel, не на прямую, а через ViewModelProvider
        viewModel = ViewModelProvider(this@CityListFragment)[WeatherListViewModel::class.java]

        // создание наблюдателя, обработка исключения, при отсутствии данных
        val observer = Observer<AppState>{
            try {
                renderData(it)
            } catch (e:IllegalStateException){
                binding.root.showSnackbar(e.message.toString(), R.string.return_loading){
                    viewModel.getCityListIf(isRussian)
                }
            }
        }

        viewModel.run {
            //создание ссылки на LiveData и передача liveDataBackground информации о владельце жизненного цикла WeatherListFragment и наблюдателе
            getLiveData().observe(viewLifecycleOwner, observer)
            //первичный запрос во WeatherListViewModel для получения информации о погоде
            loadingCityList(Location.LocationRus)
        }

        // установка слушателя на FAB
        binding.cityFAB.setOnClickListener {cityFAB ->
            isRussian = !isRussian
            isRussian.let { isRussian ->
                (cityFAB as FloatingActionButton).setImageDrawableIf(isRussian)
                viewModel.getCityListIf(isRussian)
            }
        }
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
    private fun WeatherListViewModel.getCityListIf (isRussian: Boolean){
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
    private fun renderData(appState: AppState) {
        when (appState){
            AppState.Loading -> {
                binding.showLoading()
            }
            is AppState.Success -> {
                binding.showFAB()
                setCitiesList(appState.cityList)
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
    @SuppressLint("NotifyDataSetChanged")
    private fun setCitiesList(cityList: List<City>) {
        // передача в адаптер cityList и обновление
        citiesListAdapter.setCitiesList(cityList)
        citiesListAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        _binding=null
        citiesListAdapter.removeListener()
        super.onDestroyView()
    }

}

