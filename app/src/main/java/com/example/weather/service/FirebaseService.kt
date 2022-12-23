package com.example.weather.service


import android.app.NotificationManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.weather.R
import com.example.weather.utils.CHANNEL_ID_FIREBASE
import com.example.weather.utils.TAG_LOG
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseService : FirebaseMessagingService() {

    companion object {
        private const val KEY_TITLE = "KeyTitle"
        private const val KEY_MESSAGE = "KeyMessage"
        const val NOTIFICATION_ID_FIREBASE = 2
    }

    override fun onMessageReceived(message: RemoteMessage) {
        if(message.data.isNotEmpty())
    handleDataMessage(message.data.toMap())
    }

    private fun handleDataMessage(data:Map<String, String>){
        if (!data[KEY_TITLE].isNullOrBlank() && !data[KEY_MESSAGE].isNullOrBlank())
    showNotification(data[KEY_TITLE]!!, data[KEY_MESSAGE]!!)
    }

    private fun showNotification(title:String, message:String){
        // создаем уведомление (без обработки нажатия на уведомление)
        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID_FIREBASE)
            .setSmallIcon(R.drawable.weather_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setColor(applicationContext.getColor(R.color.purple_700))
            .build()

        // создаем notificationManager
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        // запускаем уведомление
        notificationManager.notify(NOTIFICATION_ID_FIREBASE, notification)
        }

    override fun onNewToken(token: String) {
        Log.v(TAG_LOG, token)
    }
}