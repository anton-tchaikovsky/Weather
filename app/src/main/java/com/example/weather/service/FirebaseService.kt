package com.example.weather.service


import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.weather.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseService : FirebaseMessagingService() {

    companion object {
        private const val KEY_TITLE = "KeyTitle"
        private const val KEY_MESSAGE = "KeyMessage"
        private const val CHANNEL_ID_FIREBASE = "ChannelId"
        private const val NOTIFICATION_ID_FIREBASE = 37
        const val CHANNEL_NAME_FIREBASE = "ChannelName"
    }

    override fun onCreate() {
        super.onCreate()
        Log.v("@@@", "Сервис в работе")
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

        // создаем канал уведомления
        initNotificationChannel(notificationManager)

        // запускаем уведомление
        notificationManager.notify(NOTIFICATION_ID_FIREBASE, notification)
        }


    private fun initNotificationChannel(notificationManager:NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // создаем канал (без настроек)
            val channel = NotificationChannel(CHANNEL_ID_FIREBASE, CHANNEL_NAME_FIREBASE, NotificationManager.IMPORTANCE_LOW)
            // запускаем канал
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onNewToken(token: String) {
        Log.v("@@@", token)
    }
}