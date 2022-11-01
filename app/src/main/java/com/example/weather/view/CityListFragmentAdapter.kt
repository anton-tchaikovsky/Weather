package com.example.weather.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.CityItemBinding
import com.example.weather.model.City

// в конструктор передаем объект CityListFragment.OnItemCityClickListener для обработки нажатия на элемент списка
class CityListFragmentAdapter (private var itemCityClickListener: CityListFragment.OnItemCityClickListener?): RecyclerView.Adapter<CityListFragmentAdapter.ViewHolder>() {

    private var _binding: CityItemBinding? = null
    private val binding
        get() = _binding!!
    private var dataCityList:List<City> = listOf()

    // метод для получения списка данных
    fun setCitiesList(dataCityList:List<City>){
        this.dataCityList = dataCityList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        _binding = CityItemBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setCity(dataCityList[position])
    }

    override fun getItemCount(): Int {
        return dataCityList.size
    }

    // inner необходим для видимости переменных родительского класса
    inner class ViewHolder(binding: CityItemBinding) : RecyclerView.ViewHolder(binding.root){
        fun setCity (city: City){
            binding.run{
                cityName.text = city.cityName
                root.setOnClickListener {itemCityClickListener?.onItemClick(city)}
            }
        }
    }

    fun removeListener(){
        itemCityClickListener=null
        _binding = null
    }

}
