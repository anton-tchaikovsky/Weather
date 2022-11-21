package com.example.weather.model

import com.example.weather.model.dto.WeatherDTO
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

    override fun getWeatherFromServer(city: City, callback: Callback<WeatherDTO>) {
        RemoteDataWeatherSource().getWeatherFromWebService(city, callback)
    }
}

class RepositoryThemesImpl: RepositoryThemes{

    override fun getTheme(themeKey: String) =
        when(themeKey){
            Themes.SUMMER.themeKey -> Themes.SUMMER.themeRes
            Themes.WINTER.themeKey -> Themes.WINTER.themeRes
            Themes.SPRING.themeKey -> Themes.SPRING.themeRes
            Themes.AUTUMN.themeKey -> Themes.AUTUMN.themeRes
            else -> Themes.WEATHER.themeRes
        }
}