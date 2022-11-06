@file:Suppress("DEPRECATION")

package com.example.weather.service

import android.app.IntentService
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager

const val MAIN_SERVICE_STRING_EXTRA = "MainServiceExtra"

class Service(name:String = "Service"): IntentService(name) {

    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        createLog("onHandleIntent")
        // работаем с информацией из интента
        val number = (intent?.getIntExtra("MAIN_SERVICE_INT_EXTRA", 0)).toString()
        // создаем интент и рассылаем результат работы
        val broadcastIntent = Intent("TEST BROADCAST INTENT FILTER")
        broadcastIntent.putExtra("THREADS_FRAGMENT_BROADCAST_EXTRA", "Результат из сервиса $number")
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
    }

    @Deprecated("Deprecated in Java")
    override fun onCreate() {
        createLog("onCreate")
        super.onCreate()
    }

    @Deprecated("Deprecated in Java")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createLog("onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    @Deprecated("Deprecated in Java")
    override fun onDestroy() {
        createLog("onDestroy")
        super.onDestroy()
    }

    private fun createLog (message: String) = Log.v ("MyTag", message)
}