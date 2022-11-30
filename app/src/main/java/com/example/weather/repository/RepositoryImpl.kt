package com.example.weather.repository

import com.example.weather.app.AppWeather
import com.example.weather.model.RemoteDataWeatherSource
import com.example.weather.model.Themes
import com.example.weather.model.Weather
import com.example.weather.model.city.City
import com.example.weather.model.city.Location
import com.example.weather.model.city.getCityListRus
import com.example.weather.model.city.getCityListWorld
import com.example.weather.model.dto.WeatherDTO
import com.example.weather.utils.convertFromListEntityToListWeather
import com.example.weather.utils.convertFromWeatherToEntity
import retrofit2.Callback

class RepositoryLocalImpl: RepositoryCityList {
    override fun getCityList(location: Location): List<City> =
        when(location){
            Location.LocationRus -> getCityListRus()
            Location.LocationWorld -> getCityListWorld()
        }
}

class RepositoryRemoteImpl: RepositoryCityList, RepositoryWeather {

    override fun getCityList(location: Location): List<City> =
        when(location){
            Location.LocationRus -> getCityListRus()
            Location.LocationWorld -> getCityListWorld()
        }

    override fun getWeatherFromServer(city: City, callback: Callback<WeatherDTO>) =
        RemoteDataWeatherSource().getWeatherFromWebService(city, callback)

}

class RepositoryHistoryImpl:RepositoryHistory{

    override fun getAllHistoryWeather(): List<Weather> =
        convertFromListEntityToListWeather(AppWeather.getHistoryDao().getAllHistoryEntity())

    override fun getHistoryWeatherByCity(city: City): List<Weather> =
        convertFromListEntityToListWeather(AppWeather.getHistoryDao().getHistoryEntityByCity(city.cityName))

    override fun saveWeather(weather: Weather) =
        AppWeather.getHistoryDao().insertHistoryEntity(convertFromWeatherToEntity(weather))

    override fun deleteAllHistoryWeather() =
        AppWeather.getHistoryDao().let {
            it.deleteListHistoryEntity(it.getAllHistoryEntity())
        }

    override fun deleteAllHistoryWeatherByCity(city: City) =
        AppWeather.getHistoryDao().let {
            it.deleteListHistoryEntity(it.getHistoryEntityByCity(city.cityName))
        }

}

class RepositoryThemesImpl: RepositoryThemes {

    override fun getTheme(themeKey: String) =
        when(themeKey){
            Themes.SUMMER.themeKey -> Themes.SUMMER.themeRes
            Themes.WINTER.themeKey -> Themes.WINTER.themeRes
            Themes.SPRING.themeKey -> Themes.SPRING.themeRes
            Themes.AUTUMN.themeKey -> Themes.AUTUMN.themeRes
            else -> Themes.WEATHER.themeRes
        }
}