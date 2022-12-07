package com.example.weather.viewmodel

import android.os.Handler
import android.os.HandlerThread
import com.example.weather.utils.HANDLER_THREAD_HISTORY_NAME

class ThreadHistory {

    companion object {
        private var handler:Handler? = null
    }

    fun getHandler(): Handler {
        if (handler == null) {
            val handlerThreadHistory = HandlerThread(HANDLER_THREAD_HISTORY_NAME)
            handlerThreadHistory.start()
            handler = Handler(handlerThreadHistory.looper)
        }
        return handler as Handler
    }
}