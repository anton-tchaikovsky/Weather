package com.example.weather.model.room

import androidx.room.*

@Dao
interface HistoryDao {

    @Query ("SELECT * FROM HistoryEntity")
    fun getAllHistoryEntity():List<HistoryEntity>

    @Query ("SELECT * FROM HistoryEntity WHERE cityName LIKE :cityName")
    fun getHistoryEntityByCity(cityName:String): List<HistoryEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE )
    fun insertHistoryEntity(historyEntity: HistoryEntity)

    @Update
    fun updateHistoryEntity(historyEntity: HistoryEntity)

    @Delete
    fun deleteHistoryEntity(historyEntity: HistoryEntity)
}