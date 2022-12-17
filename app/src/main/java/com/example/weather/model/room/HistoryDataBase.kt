package com.example.weather.model.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 1, exportSchema = false, entities = [HistoryEntity::class])
abstract class HistoryDataBase: RoomDatabase() {
    abstract fun getHistoryDao (): HistoryDao
}