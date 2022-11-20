package com.example.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.model.Location
import com.example.weather.model.RepositoryCityList
import com.example.weather.model.RepositoryLocalImpl
import com.example.weather.model.RepositoryRemoteImpl

class CityListViewModel(

    // создание liveData для данных о списке городов
    private val liveData: MutableLiveData<AppState> =
        MutableLiveData()) : ViewModel() {

    // создание переменной для репозитория, предоставляющего данные о списке городов
    private lateinit var repositoryImpl: RepositoryCityList

    // метод для получения liveData
    fun getLiveData() = liveData

    // метод создает ссылку на репозиторий в зависимотсти от того, есть ли удаленная связь
    private fun getSelectionSource(isConnect: Boolean) {
        repositoryImpl = if (isConnect)
            RepositoryRemoteImpl()
        else
            RepositoryLocalImpl()
    }

    // метод эммулирует, есть ли удаленная связь
    private fun isConnect() = false

    // метод обеспечивает формирование данных о городах с помощью  liveData и их передачу
    fun loadingCityList(location:Location) {
        getSelectionSource(isConnect())
        if (isSuccess()){
            // эммуляция загрузки данных
            liveData.value = AppState.Loading
            Thread{
                Thread.sleep(2000)
                // передача информации об успешной загрузке данных, включающей в себя список городов
                    liveData.postValue(AppState.Success(
                        repositoryImpl.getCityList(location)
                    ))
            }.start()
        }
        else
            // эммуляция ошибки загрузки данных
            liveData.postValue(AppState.Error(IllegalStateException("Ошибка загрузки данных.")))

    }

    // метод обеспечивает передачу данных о городах (без загрузки и ошибки загрузки)
    fun getCityList(location:Location){
        liveData.value = AppState.Success(repositoryImpl.getCityList(location))
    }

    // метод эммулирует, успешно ли произошла загрузка данных о списке городов
    private fun isSuccess() = (0..2).random()!=1

}