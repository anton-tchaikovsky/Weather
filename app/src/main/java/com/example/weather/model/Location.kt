package com.example.weather.model

sealed class Location{
    object LocationRus: Location()
    object LocationWorld: Location()
}
