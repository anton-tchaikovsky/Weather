package com.example.weather.model

class RepositoryLocalImpl: RepositoryCityList {
    override fun getCityList(location: Location): List<City> =
        when(location){
            Location.LocationRus -> getCityListRus()
            Location.LocationWorld -> getCityListWorld()
        }
}

class RepositoryRemoteImpl: RepositoryCityList, RepositoryWeather {
    override fun getWeatherFromServer(requestLink: String, callback: okhttp3.Callback) {
        RemoteDataWeatherSource().getWeatherFromWebService(requestLink, callback)
    }
    override fun getCityList(location: Location): List<City> =
        when(location){
            Location.LocationRus -> getCityListRus()
            Location.LocationWorld -> getCityListWorld()
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