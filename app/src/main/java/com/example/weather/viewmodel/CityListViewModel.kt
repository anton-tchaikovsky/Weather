package com.example.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.model.city.Location
import com.example.weather.repository.*
import com.example.weather.utils.ERROR_LOADING

class CityListViewModel(

    // создание liveData для данных о списке городов и о найденом городе
    private val liveDataCityList: MutableLiveData<AppStateCityList> =
        MutableLiveData(),  private val liveDataCitySearch: MutableLiveData<AppStateCitySearch> =
        MutableLiveData())   : ViewModel() {

    // создание переменной для репозитория, предоставляющего данные о списке городов
    private val repositoryCityList = RepositoryCityListImpl()

    // создание переменной для репозитория, предоставляющего город по его названию
    private val repositoryCitySearch = RepositoryCitySearchImpl()

    // методы для получения liveData
    fun getLiveDataCityList() = liveDataCityList
    fun getLiveDataCitySearch() = liveDataCitySearch


    // метод обеспечивает формирование данных о городах с помощью  liveData и их передачу
    fun loadingCityList(location: Location) {
        if (isSuccess()){
            // эммуляция загрузки данных
            liveDataCityList.value = AppStateCityList.Loading
            Thread{
                Thread.sleep(2000)
                // передача информации об успешной загрузке данных, включающей в себя список городов
                liveDataCityList.postValue(AppStateCityList.Success(
                        repositoryCityList.getCityList(location)
                    ))
            }.start()
        }
        else
            // эммуляция ошибки загрузки данных
            liveDataCityList.postValue(AppStateCityList.Error(IllegalStateException(ERROR_LOADING)))
    }

    // метод обеспечивает передачу данных о городах (без загрузки и ошибки загрузки)
    fun getCityList(location: Location){
        liveDataCityList.value = AppStateCityList.Success(repositoryCityList.getCityList(location))
    }

    // метод эммулирует, успешно ли произошла загрузка данных о списке городов
    private fun isSuccess() = (0..2).random()!=1

    // метод передает информацию о результатах поиска города по его названию
    fun searchCity(cityName: String){
        Thread{
            try{
                val city = repositoryCitySearch.getLocation(cityName)
                liveDataCitySearch.postValue(AppStateCitySearch.Success(city))
            } catch (e: Exception){
                liveDataCitySearch.postValue(AppStateCitySearch.Error(e))
            }
        }.start()
    }

}