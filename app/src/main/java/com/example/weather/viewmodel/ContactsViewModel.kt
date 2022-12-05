package com.example.weather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.weather.repository.RepositoryContactsImpl

class ContactsViewModel (private val liveData: MutableLiveData<AppStateContacts> = MutableLiveData()):ViewModel() {

    private val repositoryContacts = RepositoryContactsImpl()

    fun getLiveData() = liveData

    fun getContacts() {
        liveData.value = AppStateContacts.Loading
        liveData.value = AppStateContacts.Contacts(repositoryContacts.getContacts())
    }
}