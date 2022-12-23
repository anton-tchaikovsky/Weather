package com.example.weather.model

import com.example.weather.R

enum class Themes(val themeRes:Int, val themeKey: String) {
    SUMMER(R.style.Theme_Weather_Summer, com.example.weather.utils.SUMMER),
    AUTUMN(R.style.Theme_Weather_Autumn, com.example.weather.utils.AUTUMN),
    SPRING(R.style.Theme_Weather_Spring, com.example.weather.utils.SPRING),
    WINTER(R.style.Theme_Weather_Winter, com.example.weather.utils.WINTER),
    WEATHER(R.style.Theme_Weather, com.example.weather.utils.WEATHER)
 }