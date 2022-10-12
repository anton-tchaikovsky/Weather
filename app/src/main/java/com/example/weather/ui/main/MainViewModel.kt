package com.example.weather.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel (private val liveDataToObserve: MutableLiveData<Any> =
                         MutableLiveData()) : ViewModel() {
    fun getLiveData(): LiveData<Any> {
        getDataFromLocalSource()
        return liveDataToObserve
    }

    private fun getDataFromLocalSource(){
        Thread{
            Thread.sleep(3000)
            liveDataToObserve.postValue(Any())
        }.start()
    }
}