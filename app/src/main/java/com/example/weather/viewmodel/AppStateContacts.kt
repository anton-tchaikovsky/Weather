package com.example.weather.viewmodel

sealed class AppStateContacts{
    data class Contacts (val listContacts: List<Pair<String, String>>): AppStateContacts()
    object Loading:AppStateContacts()
}
