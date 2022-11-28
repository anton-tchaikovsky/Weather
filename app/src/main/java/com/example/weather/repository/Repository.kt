package com.example.weather.repository

import com.example.weather.model.Weather
import com.example.weather.model.city.City
import com.example.weather.model.city.Location
import com.example.weather.model.dto.WeatherDTO
import com.example.weather.utils.API_KEY_PROPERTIES
import com.example.weather.utils.END_POINT
import com.example.weather.utils.LATITUDE
import com.example.weather.utils.LONGITUDE
import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

fun interface RepositoryWeather {
    fun getWeatherFromServer(city: City, callback: Callback<WeatherDTO>)
}

fun interface RepositoryCityList {
    fun getCityList(location: Location): List<City>
}

fun interface RepositoryThemes {
    fun getTheme(themeKey:String): Int
}

interface RepositoryHistory{
    fun getAllHistoryWeather():List<Weather>
    fun saveWeather(weather: Weather)
}

// интерфейс для запроса
interface WeatherAPI{
    @GET(END_POINT)
    fun getWeatherDTO(
        @Header(API_KEY_PROPERTIES) token: String,
        @Query(LATITUDE) lat:Double,
        @Query(LONGITUDE) lot:Double
    ): Call<WeatherDTO>
}