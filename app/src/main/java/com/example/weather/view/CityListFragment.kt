package com.example.weather.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.FragmentCityListBinding
import com.example.weather.model.getDataWeatherWorldCities

class CityListFragment : Fragment() {


    private var _binding: FragmentCityListBinding?=null
    private val binding get() = _binding!!

    private val citiesListAdapter = CityListFragmentAdapter()



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

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val citiesList:RecyclerView = binding.citiesList
        citiesListAdapter.setCitiesList(getDataWeatherWorldCities())
        citiesListAdapter.notifyDataSetChanged()
        citiesList.adapter = citiesListAdapter
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroy() {
        _binding=null
        super.onDestroy()
    }
}

