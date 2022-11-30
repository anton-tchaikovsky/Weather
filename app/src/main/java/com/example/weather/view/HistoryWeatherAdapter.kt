package com.example.weather.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.HistoryWeatherItemBinding
import com.example.weather.model.Weather
import com.example.weather.utils.translateConditionInRussian

class HistoryWeatherAdapter: RecyclerView.Adapter<HistoryWeatherAdapter.ViewHolder> (){

    private var listWeather:List<Weather> = listOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setListWeather(listWeather:List<Weather>){
        this.listWeather = listWeather
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(HistoryWeatherItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setHistory(listWeather[position])
    }

    override fun getItemCount() = listWeather.size


    inner class ViewHolder(private val binding:HistoryWeatherItemBinding):RecyclerView.ViewHolder(binding.root){
        fun setHistory(weather: Weather){
            binding.run {
                cityName.text = weather.cityName
                temperatureValue.text = weather.temperature.toString()
                condition.text = translateConditionInRussian(weather.condition)
            }
        }

    }
}