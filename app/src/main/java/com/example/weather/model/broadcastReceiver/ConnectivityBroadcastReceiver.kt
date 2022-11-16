package com.example.weather.model.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.weather.utils.isConnect

class ConnectivityBroadcastReceiver: BroadcastReceiver() {

    companion object{
        var isFirstReceive = true
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if(isFirstReceive){
            isFirstReceive = false
        } else {
            context.let {
                if (!isConnect(it))
                    Toast.makeText(it, "Отсутствует подключение к сети", Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(it, "Подключение к сети восстановлено", Toast.LENGTH_LONG).show()
            }
        }
    }
}