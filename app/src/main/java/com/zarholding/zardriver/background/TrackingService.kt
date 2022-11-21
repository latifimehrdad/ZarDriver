package com.zarholding.zardriver.background

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_HIGH
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.google.android.gms.location.*
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zardriver.R
import com.zarholding.zardriver.utility.EnumTripStatus
import com.zarholding.zardriver.view.activity.MainActivity
import com.zarholding.zardriver.view.fragment.HomeFragment


/**
 * Created by m-latifi on 11/9/2022.
 */

class TrackingService : LifecycleService(), RemoteErrorEmitter, RemoteSignalREmitter {

    private var location: Location? = null
    lateinit var signalRListener: SignalRListener

    private var fusedLocationProvider: FusedLocationProviderClient? = null


    //---------------------------------------------------------------------------------------------- onCreate
    override fun onCreate() {
        super.onCreate()
        MainActivity.remoteErrorEmitter = this
        signalRListener = SignalRListener.getInstance(this@TrackingService)
        startForeground()
    }
    //---------------------------------------------------------------------------------------------- onCreate


    //---------------------------------------------------------------------------------------------- onStartCommand
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        HomeFragment.tripStatus = EnumTripStatus.WAITING
        startSignalR()
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



    //---------------------------------------------------------------------------------------------- startSignalR
    private fun startSignalR() {
        if (!signalRListener.isConnection)
            signalRListener.startConnection()
    }
    //---------------------------------------------------------------------------------------------- startSignalR


    //---------------------------------------------------------------------------------------------- startLocationProvider
    private fun startLocationProvider() {
        HomeFragment.tripStatus = EnumTripStatus.START
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
    }
    //---------------------------------------------------------------------------------------------- startLocationProvider


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


    //---------------------------------------------------------------------------------------------- locationRequest
    private val locationRequest: LocationRequest = LocationRequest.create().apply {
        interval = 1
        fastestInterval = 1
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    //---------------------------------------------------------------------------------------------- locationRequest


    //---------------------------------------------------------------------------------------------- locationCallback
    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            if (HomeFragment.tripStatus != EnumTripStatus.START) {
                fusedLocationProvider?.removeLocationUpdates(this)
                if (signalRListener.isConnection)
                    signalRListener.stopConnection()
            }
            val locationList = locationResult.locations
            if (locationList.isNotEmpty())
                location = locationList.last()
            location?.let {
                if (signalRListener.isConnection)
                    signalRListener.sendToServer(
                        "service10",
                        it.latitude.toString(),
                        it.longitude.toString()
                    )
                else
                    Log.d("meri", "signalRListener is disconnect")
                Log.d("meri", "location : ${it.latitude} - ${it.longitude}")
            }
        }
    }
    //---------------------------------------------------------------------------------------------- locationCallback


    //---------------------------------------------------------------------------------------------- onConnectToSignalR
    override fun onConnectToSignalR() {
        startLocationProvider()
    }
    //---------------------------------------------------------------------------------------------- onConnectToSignalR



    //---------------------------------------------------------------------------------------------- onErrorConnectToSignalR
    override fun onErrorConnectToSignalR() {
        HomeFragment.tripStatus = EnumTripStatus.STOP
    }
    //---------------------------------------------------------------------------------------------- onErrorConnectToSignalR



    //---------------------------------------------------------------------------------------------- onReConnectToSignalR
    override fun onReConnectToSignalR() {
        HomeFragment.tripStatus = EnumTripStatus.WAITING
    }
    //---------------------------------------------------------------------------------------------- onReConnectToSignalR

}