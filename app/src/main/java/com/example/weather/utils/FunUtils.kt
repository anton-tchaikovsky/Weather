
package com.example.weather.utils

import android.content.Context
import android.net.ConnectivityManager

fun translateConditionInRussian(conditionInEnglish:String) = conditionMap[conditionInEnglish]

@Suppress("DEPRECATION")
fun isConnect(context: Context?):Boolean{
    val connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val connectInfo = connectivityManager.activeNetworkInfo
    return (connectInfo != null) && connectInfo.isConnectedOrConnecting
}


val conditionMap = hashMapOf(
    "clear" to "ясно",
    "partly-cloudy" to  "малооблачно",
    "cloudy" to "облачно с прояснениями",
    "overcast" to "пасмурно",
    "drizzle" to "морось",
    "light-rain" to "небольшой дождь",
    "rain" to "дождь",
    "moderate-rain" to "умеренно сильный дождь",
    "heavy-rain" to "сильный дождь",
    "continuous-heavy-rain" to "длительный сильный дождь",
    "showers" to "ливень",
    "wet-snow" to "дождь со снегом",
    "light-snow" to "небольшой снег",
    "snow" to "снег",
    "snow-showers" to "снегопад",
    "hail" to "град",
    "thunderstorm" to "гроза",
    "thunderstorm-with-rain" to "дождь с грозой",
    "thunderstorm-with-hail" to "гроза с градом"
)
