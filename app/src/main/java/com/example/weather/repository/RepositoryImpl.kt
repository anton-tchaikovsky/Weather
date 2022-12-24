package com.example.weather.repository

import android.annotation.SuppressLint
import android.location.Geocoder
import android.provider.ContactsContract
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

class RepositoryCityListImpl: RepositoryCityList {
    override fun getCityList(location: Location): List<City> =
        when(location){
            Location.LocationRus -> getCityListRus()
            Location.LocationWorld -> getCityListWorld()
        }
}


class RepositoryWeatherImpl: RepositoryWeather {
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

class RepositoryContactsImpl: RepositoryContacts {
    private val context = AppWeather.ProviderContextImpl.context
    @SuppressLint("Range")
    override fun getContacts(): List<Pair<String, String>> {
        val contactsList: MutableList<Pair<String, String>> = mutableListOf()
       // массив запрашиваемых данных
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        context.let {
            val contentResolver = it.contentResolver
            val cursorContacts = contentResolver.query(
                ContactsContract.CommonDataKinds
                    .Phone.CONTENT_URI,
                projection,
                null,
                null,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
            )
            cursorContacts?.let { cursor ->
                if (cursor.moveToFirst()) {
                    do {
                        // проверяем, что контакт содержит номер телефона
                        if (Integer.parseInt(cursor.getString(cursor.getColumnIndex(projection[0]))) > 0) {
                            contactsList.add(
                                Pair(
                                    cursor.getString(cursor.getColumnIndex(projection[1])),
                                    cursor.getString(cursor.getColumnIndex(projection[2]))
                                )
                            )
                        }
                    } while (cursor.moveToNext())
                }
            }
            cursorContacts?.close()
            return contactsList.toList()
        }
    }
}

class RepositoryCitySearchImpl:RepositoryCitySearch{
    private val context = AppWeather.ProviderContextImpl.context
    override fun getLocation(cityName: String): City {
        val geocoder = Geocoder(context)
        val address = geocoder.getFromLocationName(cityName, 1)[0]
        return City(address.locality, address.latitude, address.longitude)
    }
}

class RepositoryThemesImpl : RepositoryThemes {

    override fun getTheme(themeKey: String) =
        when (themeKey) {
            Themes.SUMMER.themeKey -> Themes.SUMMER.themeRes
            Themes.WINTER.themeKey -> Themes.WINTER.themeRes
            Themes.SPRING.themeKey -> Themes.SPRING.themeRes
            Themes.AUTUMN.themeKey -> Themes.AUTUMN.themeRes
            else -> Themes.WEATHER.themeRes
        }
}