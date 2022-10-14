package com.example.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.model.RepositoryImpl

class MainViewModel (private val liveDataToObserve: MutableLiveData<AppState> =
                         MutableLiveData(), private val repositoryImpl: RepositoryImpl = RepositoryImpl()
) : ViewModel() {

    fun getLiveData() = liveDataToObserve

    fun getWeatherFromLocalSource() = getDataFromLocalSource()
   // fun getWeatherFromRemoteSource() = getDataFromRemoteSource()

    private fun getDataFromLocalSource(){
        liveDataToObserve.value = AppState.Loading
        Thread{
            Thread.sleep(3000)
            liveDataToObserve.postValue(AppState.Success(repositoryImpl.getWeatherFromLocalStorage()))
        }.start()
    }
}