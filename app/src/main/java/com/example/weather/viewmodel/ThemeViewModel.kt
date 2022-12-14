package com.example.weather.viewmodel

import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.example.weather.repository.RepositoryThemes
import com.example.weather.repository.RepositoryThemesImpl


class ThemeViewModel: ViewModel() {

    // создание RepositoryThemes для доступа к данным по темам
    private val repositoryThemes: RepositoryThemes = RepositoryThemesImpl()

    // метод обеспечивает формирование данных о сезоне
    @RequiresApi(Build.VERSION_CODES.N)
    fun getTheme()=
        when (Calendar.getInstance().get(Calendar.MONTH)) {
            in (0..1) -> repositoryThemes.getTheme("winter")
            11 -> repositoryThemes.getTheme("winter")
            in (2..4) -> repositoryThemes.getTheme("spring")
            in (5..7) -> repositoryThemes.getTheme("summer")
            in (8..10) -> repositoryThemes.getTheme("autumn")
            else -> repositoryThemes.getTheme("weather")
        }
}