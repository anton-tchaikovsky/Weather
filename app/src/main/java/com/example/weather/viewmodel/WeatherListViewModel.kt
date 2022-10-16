package com.example.weather.viewmodel

import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.model.Repository
import com.example.weather.model.RepositoryLocalImpl
import com.example.weather.model.RepositoryRemoteImpl

class WeatherListViewModel(
    // создание liveData для данных о погоде
    private val liveData: MutableLiveData<AppState> =
        MutableLiveData(),
    // создание liveData для данных о фоне
    private val liveDataBackground: MutableLiveData<Seasons> = MutableLiveData()
) : ViewModel() {

    // создание переменной для репозитория, предоставляющего данные о погоде
    private lateinit var repositoryImpl: Repository

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

    // метод обеспечивает формирование данных о состоянии загрузки данных о погоде с помощью  liveData и передачу данных о погоде
    fun getDataWeather() {
        getSelectionSource(isConnect())

        if (isSuccess()){
            // эммуляция загрузки данных
            liveData.value = AppState.Loading
            Thread{
                Thread.sleep(2000)
                // передача информации об успешной загрузке данных, включающей сами данные о погоде
                liveData.postValue(AppState.Success(repositoryImpl.getWeather()))
            }.start()
        }
        else
            // эммуляция ошибки загрузки данных
            liveData.postValue(AppState.Error(throw IllegalStateException("Ошибка загрузки данных.")))

    }

    // метод эммулирует, успешно ли произошла загрузка данных о погоде
    private fun isSuccess(): Boolean {
        val i = (0..2).random()
        return i!=1
    }

    // метод обеспечивает формирование данных о сезоне и их рассылку с помощью liveDataBackground
    @RequiresApi(Build.VERSION_CODES.N)
    fun getBackground(){
        val calendar: Calendar = Calendar.getInstance()
        when(calendar.get(Calendar.MONTH)){
            in (0..1)  -> liveDataBackground.value = Seasons.Winter
            11 -> liveDataBackground.value = Seasons.Winter
            in (2..4) -> liveDataBackground.value = Seasons.Spring
            in (5..7) -> liveDataBackground.value = Seasons.Summer
            in (8..10) -> liveDataBackground.value = Seasons.Autumn
        }
    }
}