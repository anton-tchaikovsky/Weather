@file:Suppress("DEPRECATION")

package com.example.weather.service

import android.app.IntentService
import android.content.Intent
import android.util.Log

const val MAIN_SERVICE_STRING_EXTRA = "MainServiceExtra"

class Service(name:String = "Service"): IntentService(name) {

    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        createLog("onHandleIntent ${intent?.getStringExtra(MAIN_SERVICE_STRING_EXTRA)}")
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