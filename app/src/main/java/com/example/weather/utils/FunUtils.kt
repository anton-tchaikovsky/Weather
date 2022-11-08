package com.example.weather.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.weather.model.City
import java.io.BufferedReader
import java.util.stream.Collectors

@RequiresApi(Build.VERSION_CODES.N)
fun getLines(reader: BufferedReader): String {
    return reader.lines().collect(Collectors.joining("\n"))
}

fun translateConditionInRussian(conditionInEnglish:String) = conditionMap[conditionInEnglish]

fun setURL(city:City) = "https://api.weather.yandex.ru/v2/informers?lat=${city.lat}&lon=${city.lon}&[lang=<ru_RU>]"

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
