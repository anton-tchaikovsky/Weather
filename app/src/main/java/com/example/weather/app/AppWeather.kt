package com.example.weather.app

import android.app.Application
import androidx.room.Room
import com.example.weather.model.room.HistoryDao
import com.example.weather.model.room.HistoryDataBase
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

class AppWeather: Application() {

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }

    companion object{
        private  lateinit var appInstance:Application
        private var dbHistory:HistoryDataBase? = null
        private const val DB_NAME = "HistoryDB"

        @OptIn(InternalCoroutinesApi::class)
        fun getHistoryDao():HistoryDao{
            if (dbHistory==null){
                synchronized(HistoryDataBase::class.java){
                    if(dbHistory==null){
                        dbHistory = Room.databaseBuilder(
                            appInstance.applicationContext,
                            HistoryDataBase::class.java,
                            DB_NAME)
                            .allowMainThreadQueries()
                            .build()
                    }
                }
            }
            return dbHistory!!.getHistoryDao()
        }
    }
}