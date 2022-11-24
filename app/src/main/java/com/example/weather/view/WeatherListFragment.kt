package com.example.weather.view

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.weather.R
import com.example.weather.databinding.WeatherFragmentMainBinding
import com.example.weather.model.city.City
import com.example.weather.model.dto.WeatherDTO
import com.example.weather.utils.*
import com.example.weather.viewmodel.LoadingState
import com.example.weather.viewmodel.WeatherViewModel
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
        // получаем конкретный город (с проверкой на null)
        arguments?.getParcelable<City>(BUNDLE_EXTRA)?.let {
            city = it
        }

        val viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        viewModel.run {
            getLiveData().observe(viewLifecycleOwner) { renderData(it) }
            getWeatherDTO(city)
        }
    }

    private fun renderData(loadingState: LoadingState) {
        when (loadingState) {
            is LoadingState.Error -> onFailed(loadingState.error)
            is LoadingState.Success -> setWeatherForView(city, loadingState.weatherDTO)
        }
    }

    // отрисовка данных о погоде в конкретном городе
    private fun setWeatherForView(city: City, weatherDTO: (WeatherDTO)) {
        // делаем невидимым progressBar
        binding.progressBar.visibility = View.GONE

        with(binding) {

            // отрисовываем данные о конкретном городе
                cityName.text = city.cityName
            // отрисовываем данные о погоде в городе
                weatherDTO.fact.let {
                    temperatureValue.text = it.temp.toString()
                    feelsLikeValue.text = it.feels_like.toString()
                    weatherCondition.text = translateConditionInRussian(it.condition)
                    //отрисовка иконки состояния погоды
                    conditionIcon.loadSvg(String.format(CONDITION_ICON_URL,it.icon))
                }
                // видимость label
                temperatureLabel.visibility = View.VISIBLE
                feelsLikeLabel.visibility = View.VISIBLE

            }
    }

    // создание диалогового окна на случай отсутствия подключения к интернету
    private fun createAlertDialogForNoNetworkConnection() {
        AlertDialog.Builder(requireContext())
            .setTitle(MESSAGE_DISCONNECT)
            .setIcon(R.drawable.ic_baseline_error_24)
            .setMessage(MESSAGE_MAKE_CONNECT)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok)
             { _, _ -> requireActivity().supportFragmentManager.popBackStack()}
            .setNegativeButton(EXIT_APP) { _, _ -> activity?.finish() }
            .show()
    }

    // создание диалогового окна на случай ошибок получения данных из интернета, требующих внесения программных изменений в запрос
    private fun createAlertDialogForNoOtherErrors() {
        AlertDialog.Builder(requireContext())
            .setTitle(MESSAGE_ERROR)
            .setIcon(R.drawable.ic_baseline_error_24)
            .setMessage(MESSAGE_APP_CLOSE)
            .setCancelable(false)
            .setPositiveButton(android.R.string.ok)
            { _, _ -> activity?.finish() }
            .show()
    }

    // метод определяет источник ошибки и открывает соответствующее диалоговое окно
    private fun onFailed(throwable: Throwable) {
        // делаем невидимым progressBar
        binding.progressBar.visibility = View.GONE
        if ((throwable is UnknownHostException) && !isConnect(context))
            createAlertDialogForNoNetworkConnection()
        else
            createAlertDialogForNoOtherErrors()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}


