package com.example.weather.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.R
import com.example.weather.databinding.HistoryWeatherFragmentBinding
import com.example.weather.model.Weather
import com.example.weather.model.city.City
import com.example.weather.utils.CANCEL
import com.example.weather.utils.DELETE_ALL
import com.example.weather.utils.DELETE_ALL_COMPLETED
import com.example.weather.viewmodel.HistoryWeatherViewModel

class HistoryWeatherFragment : Fragment() {

    companion object {
        private const val KEY_CITY = "KeyCity"
        // фрагмент для истории по всем городам
        fun newInstance() = HistoryWeatherFragment()
        // фрагмент для истории по конкретному городу
        fun newInstance(city: City) = HistoryWeatherFragment().apply {
            arguments = Bundle().apply { putParcelable(KEY_CITY, city) }
        }
    }

    private var _binding: HistoryWeatherFragmentBinding? = null
    private val binding get() = _binding!!

    private var city: City? = null

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

        viewModel.getLiveData().observe(viewLifecycleOwner) {
            renderData(it)
        }

        arguments?.getParcelable<City>(KEY_CITY)?.let {
            city = it
            viewModel.getHistoryWeatherByCity(it)
        }

        if (city == null)
            viewModel.getHistoryWeather()
    }

    private fun renderData(listWeather: List<Weather>) {
        historyWeatherAdapter.let {
            it.setListWeather(listWeather)
            if (it.itemCount == 0)
                Toast.makeText(requireContext(), DELETE_ALL_COMPLETED, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        // скрываем элементы history_menu и contacts_menu
        menu.run {
            findItem(R.id.history_menu)?.isVisible = false
            findItem(R.id.contacts_menu)?.isVisible = false
        }
        inflater.inflate(R.menu.delete_all_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.delete_all_menu) {
            createAlertDialogForDeleteAllHistoryWeather()
            true
        } else
            super.onOptionsItemSelected(item)
    }

    private fun createAlertDialogForDeleteAllHistoryWeather() {
        if (historyWeatherAdapter.itemCount > 0)
            AlertDialog.Builder(requireContext())
                .setTitle(DELETE_ALL)
                .setPositiveButton(android.R.string.ok)
                { _, _ ->
                    city?.let {
                        viewModel.deleteHistoryByCity(it)
                    }
                    if (city == null)
                        viewModel.deleteAllHistoryWeather()
                    Toast.makeText(requireContext(), DELETE_ALL_COMPLETED, Toast.LENGTH_SHORT)
                        .show()
                }
                .setNegativeButton(CANCEL) { dialog, _ -> dialog.dismiss() }
                .show()
        else
            Toast.makeText(requireContext(), DELETE_ALL_COMPLETED, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
