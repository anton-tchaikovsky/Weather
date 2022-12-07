package com.example.weather.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.weather.model.room.HistoryDao
import com.example.weather.model.room.HistoryDataBase
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.synchronized

class AppWeather: Application() {

    override fun onCreate() {
        super.onCreate()
        appInstance = this
        contextApp = this.applicationContext
    }

    companion object{
        @SuppressLint("StaticFieldLeak")
        private lateinit var contextApp: Context
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
                            .build()
                    }
                }
            }
            return dbHistory!!.getHistoryDao()
        }
    }
    interface ProviderContext{
        val context:Context
    }

    object ProviderContextImpl: ProviderContext{
        override val context: Context
            get() = contextApp

    }
}