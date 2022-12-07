package com.example.weather.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val cityName:String = "",
    val temperature: Double = 0.0,
    val condition: String = ""
)
