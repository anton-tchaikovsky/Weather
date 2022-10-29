package com.example.weather.viewmodel

import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.model.Location
import com.example.weather.model.RepositoryCityList
import com.example.weather.model.RepositoryLocalImpl
import com.example.weather.model.RepositoryRemoteImpl

class WeatherListViewModel(

    // создание liveData для данных о погоде
    private val liveData: MutableLiveData<AppState> =
        MutableLiveData(),
    // создание liveDataBackground для данных о фоне
    private val liveDataBackground: MutableLiveData<Seasons> = MutableLiveData()
) : ViewModel() {

    // создание переменной для репозитория, предоставляющего данные о погоде
    private lateinit var repositoryImpl: RepositoryCityList

    // метод для получения liveData
    fun getLiveData() = liveData

    // метод для получения liveDataBackground
    fun getLiveDataBackground() = liveDataBackground

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

    // метод эммулирует, успешно ли произошла загрузка данных о погоде
    private fun isSuccess() = (0..2).random()!=1

    // метод обеспечивает формирование данных о сезоне и их рассылку с помощью liveDataBackground
    @RequiresApi(Build.VERSION_CODES.N)
    fun getBackground(){
        when( Calendar.getInstance().get(Calendar.MONTH)){
            in (0..1)  -> liveDataBackground.value = Seasons.Winter
            11 -> liveDataBackground.value = Seasons.Winter
            in (2..4) -> liveDataBackground.value = Seasons.Spring
            in (5..7) -> liveDataBackground.value = Seasons.Summer
            in (8..10) -> liveDataBackground.value = Seasons.Autumn
        }
    }
}