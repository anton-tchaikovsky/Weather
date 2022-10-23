package com.example.weather.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.model.Weather

// в конструктор передаем объект CityListFragment.OnItemCityClickListener для обработки нажатия на элемент списка
class CityListFragmentAdapter (private var itemCityClickListener: CityListFragment.OnItemCityClickListener?): RecyclerView.Adapter<CityListFragmentAdapter.ViewHolder>() {

    private var dataWeatherList:List<Weather> = listOf()

    // метод для получения списка данных
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

    // inner необходим для видимости переменных родительского класса
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun setCity (weather: Weather){
            itemView.findViewById<CheckedTextView>(R.id.city_name).text = weather.city.cityName
            itemView.setOnClickListener {itemCityClickListener?.onItemClick(weather)}
        }
    }

    fun removeListener(){
        itemCityClickListener=null
    }

}
