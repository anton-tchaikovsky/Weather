package com.example.weather.model.room

import android.database.Cursor
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

    @Delete
    fun deleteListHistoryEntity(listHistoryEntity: List<HistoryEntity>)

    @Query("DELETE FROM HistoryEntity WHERE id = :id")
    fun deleteHistoryEntityById(id:Long)

    @Query("SELECT * FROM HistoryEntity WHERE id = :id")
    fun getCursorHistoryEntity(id:Long):Cursor

    @Query("SELECT * FROM HistoryEntity")
    fun getCursorHistoryEntity():Cursor

}