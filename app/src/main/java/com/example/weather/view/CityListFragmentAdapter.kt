package com.example.weather.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.databinding.CityItemBinding
import com.example.weather.model.Weather

class CityListFragmentAdapter (private var itemCityClickListener: CityListFragment.OnItemCityClickListener?): RecyclerView.Adapter<CityListFragmentAdapter.ViewHolder>() {

    private var dataWeatherList:List<Weather> = listOf()

    fun setCitiesList(dataWeatherList:List<Weather>){
        this.dataWeatherList = dataWeatherList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.city_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setCity(dataWeatherList[position])
    }

    override fun getItemCount(): Int {
        return dataWeatherList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        private var binding:CityItemBinding = CityItemBinding.bind(itemView)

        fun setCity (weather: Weather){
            binding.cityName.text = weather.city.cityName
            itemView.setOnClickListener {itemCityClickListener?.onClick(weather)}
        }

    }

    fun removeListener(){
        itemCityClickListener=null
    }

}
