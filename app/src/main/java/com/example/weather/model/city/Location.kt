package com.example.weather.model.city

sealed class Location{
    object LocationRus: Location()
    object LocationWorld: Location()
}
