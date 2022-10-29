package com.example.weather.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckedTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.model.City

// в конструктор передаем объект CityListFragment.OnItemCityClickListener для обработки нажатия на элемент списка
class CityListFragmentAdapter (private var itemCityClickListener: CityListFragment.OnItemCityClickListener?): RecyclerView.Adapter<CityListFragmentAdapter.ViewHolder>() {

    private var dataCityList:List<City> = listOf()

    // метод для получения списка данных
    fun setCitiesList(dataCityList:List<City>){
        this.dataCityList = dataCityList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.city_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setCity(dataCityList[position])
    }

    override fun getItemCount(): Int {
        return dataCityList.size
    }

    // inner необходим для видимости переменных родительского класса
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun setCity (city: City){
            itemView.findViewById<CheckedTextView>(R.id.city_name).text = city.cityName
            itemView.setOnClickListener {itemCityClickListener?.onItemClick(city)}
        }
    }

    fun removeListener(){
        itemCityClickListener=null
    }

}
