package com.zarholding.zardriver.tools.background

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
import com.zarholding.zardriver.R
import com.zarholding.zardriver.model.response.TripModel
import com.zarholding.zardriver.tools.CompanionValues
import com.zarholding.zardriver.tools.EnumTripStatus
import com.zarholding.zardriver.tools.LocationTool
import com.zarholding.zardriver.view.fragment.home.HomeFragment


/**
 * Created by m-latifi on 11/9/2022.
 */

class TrackingService : LifecycleService(), RemoteSignalREmitter {

    private var location: Location? = null
    lateinit var signalRListener: SignalRListener

    private var fusedLocationProvider: FusedLocationProviderClient? = null

    private var token: String? = null
    private var tripModel: TripModel? = null
    private var driverId: Int? = null


    //---------------------------------------------------------------------------------------------- onCreate
    override fun onCreate() {
        super.onCreate()
        HomeFragment.tripStatus = EnumTripStatus.WAITING
        startForeground()
    }
    //---------------------------------------------------------------------------------------------- onCreate


    //---------------------------------------------------------------------------------------------- onStartCommand
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        token = intent?.extras?.getString(CompanionValues.TOKEN, null)
        tripModel = intent?.extras?.getParcelable(CompanionValues.tripModel)
        driverId = intent?.extras?.getInt(CompanionValues.driverId)
        signalRListener = SignalRListener.getInstance(this@TrackingService, token)
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
            .setContentTitle(applicationContext.resources.getString(R.string.appIsRunning))
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

        tripModel?.stations?.get(0)?.let {
            Log.i("meri", "stationId = ${it.id}")
            sendNotificationToServer(it.id)
        }
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
                    sendToServer(it)
            }
        }
    }
    //---------------------------------------------------------------------------------------------- locationCallback


    //---------------------------------------------------------------------------------------------- sendToServer
    private fun sendToServer(currentLocation: Location) {
        var stationId = tripModel?.let {
            LocationTool().checkLocationNearbyStation(it.stations, currentLocation)
        } ?: run { -1 }
        if (stationId == -1)
            sendPointToServer(currentLocation)
        else {
            stationId = tripModel!!.stations!![stationId].id
            sendNotificationToServer(stationId)
        }
    }
    //---------------------------------------------------------------------------------------------- sendToServer


    //---------------------------------------------------------------------------------------------- sendPointToServer
    private fun sendPointToServer(location: Location) {
        signalRListener.sendToServer(
            tripModel?.id,
            driverId,
            location.latitude.toString(),
            location.longitude.toString()
        )
    }
    //---------------------------------------------------------------------------------------------- sendPointToServer


    //---------------------------------------------------------------------------------------------- sendNotificationToServer
    private fun sendNotificationToServer(stationId: Int) {
        signalRListener.NotificationToServer(tripModel?.id, stationId)
    }
    //---------------------------------------------------------------------------------------------- sendNotificationToServer


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
        if (HomeFragment.tripStatus != EnumTripStatus.STOP)
            HomeFragment.tripStatus = EnumTripStatus.RECONNECT
    }
    //---------------------------------------------------------------------------------------------- onReConnectToSignalR

}