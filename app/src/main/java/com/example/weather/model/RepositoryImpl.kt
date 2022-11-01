package com.example.weather.model

class RepositoryLocalImpl: RepositoryCityList, RepositoryWeather {
    override fun getWeather(lat:Double, lon:Double): Weather = Weather()
    override fun getCityList(location: Location): List<City> =
        when(location){
            Location.LocationRus -> getCityListRus()
            Location.LocationWorld -> getCityListWorld()
        }
}

class RepositoryRemoteImpl: RepositoryCityList, RepositoryWeather {
    override fun getWeather(lat:Double, lon:Double)= Weather()
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