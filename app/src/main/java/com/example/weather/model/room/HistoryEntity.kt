package com.example.weather.model.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val cityName:String,
    val temperature: Double,
    val condition: String
)
