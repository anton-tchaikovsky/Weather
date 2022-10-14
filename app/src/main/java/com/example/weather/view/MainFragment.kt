package com.example.weather.view

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.weather.R
import com.example.weather.databinding.FragmentMainBinding
import com.example.weather.model.Weather
import com.example.weather.viewmodel.AppState
import com.example.weather.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar

class MainFragment : Fragment() {

    companion object {
       fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        val observer = Observer<AppState> { renderData (it) }
        val liveDataToObserve =  viewModel.getLiveData()
        liveDataToObserve.observe(viewLifecycleOwner, observer)
        viewModel.getWeatherFromLocalSource()
        // TODO: Use the ViewModel
    }

    private fun renderData(appState: AppState) {
        val loadingLayout = binding.loadingLayout
        val mainView = binding.mainView
        when (appState){
            is AppState.Loading -> loadingLayout.visibility = View.VISIBLE
            is AppState.Success -> {
                val weatherData = appState.weatherData
                loadingLayout.visibility = View.GONE
                setData(weatherData)
            }
            is AppState.Error -> {
                loadingLayout.visibility = View.GONE
                Snackbar
                    .make(mainView, "Error", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Reload") { viewModel.getWeatherFromLocalSource() }
                    .show()
            }
        }
    }

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


