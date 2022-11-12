package com.zarholding.zardriver.background

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.zarholding.zardriver.R
import com.zarholding.zardriver.view.fragment.HomeFragment
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO


/**
 * Created by m-latifi on 11/9/2022.
 */

class TrackingService : Service() {

    private val binder: Binder = TimerBinder()
    override fun onBind(p0: Intent?): IBinder = binder


    private var fusedLocationProvider: FusedLocationProviderClient? = null
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 30
        fastestInterval = 10
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }
    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (!HomeFragment.driving)
                fusedLocationProvider?.removeLocationUpdates(this)
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                val location = locationList.last()
                Log.d("meri", location.toString())
            }
        }
    }


    //---------------------------------------------------------------------------------------------- Binder
    inner class TimerBinder : Binder() {
        val service: TrackingService
            get() = this@TrackingService
    }
    //---------------------------------------------------------------------------------------------- Binder


    //---------------------------------------------------------------------------------------------- onCreate
    override fun onCreate() {
        super.onCreate()
        startForeground()
    }
    //---------------------------------------------------------------------------------------------- onCreate


    //---------------------------------------------------------------------------------------------- onStartCommand
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("meri", "onStartCommand")
        HomeFragment.driving = true
/*        CoroutineScope(IO).launch {
            repeat(1000) {
                if (!HomeFragment.driving) {
                    Log.d("meri", "cancel")
                    stopForeground(true)
                    stopSelf()
                    cancel()
                    delay(2000)
                } else {
                    HomeFragment.timeSecond++
                    Log.d("meri", "Looper")
                    delay(1000)
                }
            }
        }*/
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {

            fusedLocationProvider?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
        return super.onStartCommand(intent, flags, startId)
    }
    //---------------------------------------------------------------------------------------------- onStartCommand


    //---------------------------------------------------------------------------------------------- startForeground
    private fun startForeground() {
        val channelId = createNotificationChannel()
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        val notification = notificationBuilder.setOngoing(true)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(PRIORITY_HIGH)
            .setContentTitle("Driver is running ....")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(101, notification)
    }
    //---------------------------------------------------------------------------------------------- startForeground


    //---------------------------------------------------------------------------------------------- createNotificationChannel
    private fun createNotificationChannel(): String {
        val channelId = "ZarTrackingServiceId"
        val channelName = "Zar Background Service"
        val chan = NotificationChannel(
            channelId,
            channelName, NotificationManager.IMPORTANCE_HIGH
        )
        chan.lightColor = Color.BLUE
        chan.importance = NotificationManager.IMPORTANCE_NONE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }
    //---------------------------------------------------------------------------------------------- createNotificationChannel


}