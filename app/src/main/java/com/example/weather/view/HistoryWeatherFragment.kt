package com.example.weather.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.R
import com.example.weather.databinding.HistoryWeatherFragmentBinding
import com.example.weather.model.Weather
import com.example.weather.viewmodel.HistoryWeatherViewModel

class HistoryWeatherFragment : Fragment() {

    companion object {
        fun newInstance() = HistoryWeatherFragment()
    }

    private var _binding: HistoryWeatherFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HistoryWeatherViewModel by lazy { ViewModelProvider(this)[HistoryWeatherViewModel::class.java] }

    private val historyWeatherAdapter = HistoryWeatherAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //включаем меню
        setHasOptionsMenu(true)
        _binding = HistoryWeatherFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.historyWeatherList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyWeatherAdapter
        }

        viewModel.run {
            getLiveData().observe(viewLifecycleOwner) {
                renderData(it)
            }
            getHistoryWeather()
        }
    }

    private fun renderData(listWeather: List<Weather>) {
        historyWeatherAdapter.setListWeather(listWeather)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.history_menu)?.isVisible = false
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
