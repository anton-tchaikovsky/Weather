package com.example.weather.broadcastReceiver

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.weather.R
import com.example.weather.utils.CHANNEL_ID
import com.example.weather.utils.MESSAGE_LAUNCH_APP_TEXT
import com.example.weather.utils.MESSAGE_LAUNCH_APP_TITLE
import com.example.weather.view.WeatherActivity

class BroadcastReceiverForStartApp : BroadcastReceiver() {

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onReceive(context: Context, intent: Intent) {
        // создаем PendingIntent с интентом для запуска WeatherActivity
        val startAppPendingIntent =
            PendingIntent.getActivity(context, 0, Intent(context, WeatherActivity::class.java), 0)

        // создаем уведомление
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.weather_icon)
            .setContentTitle(MESSAGE_LAUNCH_APP_TITLE)
            .setContentText(MESSAGE_LAUNCH_APP_TEXT)
            .setContentIntent(startAppPendingIntent)
            .setAutoCancel(true)
            .setColor(context.getColor(R.color.purple_700))
            .build()

        // создаем notificationManager и запускаем уведомление
        (context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager).run {
            notify(1, notification)
        }

    }
}