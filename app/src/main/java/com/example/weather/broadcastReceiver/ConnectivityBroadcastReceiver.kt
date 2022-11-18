package com.example.weather.broadcastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.weather.utils.MESSAGE_CONNECT
import com.example.weather.utils.MESSAGE_DISCONNECT
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
                    Toast.makeText(it, MESSAGE_DISCONNECT, Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(it, MESSAGE_CONNECT, Toast.LENGTH_LONG).show()
            }
        }
    }
}