package com.example.geolocationservice.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import com.example.geolocationservice.Constants
import com.example.geolocationservice.R

class LocationService: LifecycleService()
{
    companion object
    {
        var running: Boolean = false
    }

    private fun locationChanged(l: Location)
    {
        LocationData.location.postValue(l)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        intent?.apply {
            when (action) {
                Constants.START_LOCATION_SERVICE -> {
                    startLocationService(this)
                }
                Constants.STOP_LOCATING_SERVICE -> {
                    stopLocationService(this)
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startLocationService(intent: Intent)
    {
        if (LocationHelper.startLocating(applicationContext, ::locationChanged)) {
            running = true
            val channelId =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createNotificationChannel()
                else ""
            val notificationBuilder = NotificationCompat.Builder(this, channelId)
            val notification = notificationBuilder
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_start_location)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .build()
            startForeground(Constants.FOREGROUND_ID, notification)  // запуск службы
        }
    }

    private fun stopLocationService(intent: Intent)
    {
        running = false
        LocationHelper.stopLocating()
        stopService(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): String
    {
        val channel = NotificationChannel(
            Constants.LOCATION_CHANNEL_ID,
            getString(R.string.location_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT)

        (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .createNotificationChannel(channel)

        return Constants.LOCATION_CHANNEL_ID
    }
}