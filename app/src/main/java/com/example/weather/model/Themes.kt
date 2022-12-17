package com.example.weather.model

import com.example.weather.R

enum class Themes(val themeRes:Int, val themeKey: String) {
    SUMMER(R.style.Theme_Weather_Summer, "summer"),
    AUTUMN(R.style.Theme_Weather_Autumn, "autumn"),
    SPRING(R.style.Theme_Weather_Spring, "spring"),
    WINTER(R.style.Theme_Weather_Winter, "winter"),
    WEATHER(R.style.Theme_Weather, "weather")
 }