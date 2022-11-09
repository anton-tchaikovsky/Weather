package com.example.weather.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder

class FibonacciService : Service() {

    private val binder:IBinder = ServiceBinder()

    private var fibonacci1:Long = 0
    private var fibonacci2:Long = 1

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    fun getNextFibonacci (): Long{
        val result = fibonacci1.plus(fibonacci2)
        fibonacci1 = fibonacci2
        fibonacci2 = result
        return result
    }

    class ServiceBinder: Binder(){
       fun getService() = FibonacciService()
        //fun getNextFibonacci (): Long = getService().getNextFibonacci()
    }
}