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
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zardriver.R
import com.zarholding.zardriver.view.activity.MainActivity
import com.zarholding.zardriver.view.fragment.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject
import androidx.lifecycle.observe
import com.zarholding.zardriver.model.request.TrackDriverRequestModel
import com.zarholding.zardriver.repository.TrackDriverRepository
import com.zarholding.zardriver.viewmodel.TrackingDriverViewModel


/**
 * Created by m-latifi on 11/9/2022.
 */

@AndroidEntryPoint
class TrackingService : LifecycleService(), RemoteErrorEmitter, RemoteSignalREmitter {

    lateinit var job: Job
    private var location: Location? = null
    lateinit var signalRListener: SignalRListener

    //    private lateinit var locationManager: GeoLocationManager
    private var fusedLocationProvider: FusedLocationProviderClient? = null

    @Inject
    lateinit var repository: TrackDriverRepository

    lateinit var trackingDriverViewModel: TrackingDriverViewModel


    //---------------------------------------------------------------------------------------------- onCreate
    override fun onCreate() {
        super.onCreate()
        trackingDriverViewModel = TrackingDriverViewModel(repository)
        MainActivity.remoteErrorEmitter = this
//        locationManager = GeoLocationManager(applicationContext)
        signalRListener = SignalRListener.getInstance(this@TrackingService)
        startForeground()
//        createJob()
    }
    //---------------------------------------------------------------------------------------------- onCreate


    //---------------------------------------------------------------------------------------------- onStartCommand
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        HomeFragment.driving = true
        fusedLocationProvider = LocationServices.getFusedLocationProviderClient(this)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            if (!signalRListener.isConnection)
                signalRListener.startConnection()

//            locationManager.startLocationTracking(locationCallback)

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
            if (!HomeFragment.driving) {
                fusedLocationProvider?.removeLocationUpdates(this)
                if (signalRListener.isConnection)
                    signalRListener.stopConnection()
//                locationManager.stopLocationTracking()
            }
            val locationList = locationResult.locations
            if (locationList.isNotEmpty())
                location = locationList.last()
            location?.let {
                if (signalRListener.isConnection)
                    signalRListener.sendToServer(it.latitude.toFloat(), it.longitude.toFloat())
                Log.d("meri", "location : ${it.latitude} - ${it.longitude}")
            }
        }
    }
    //---------------------------------------------------------------------------------------------- locationCallback


    //---------------------------------------------------------------------------------------------- createJob
    private fun createJob() {
        job = Job()
        counter(job)
    }
    //---------------------------------------------------------------------------------------------- createJob


    //---------------------------------------------------------------------------------------------- counter
    private fun counter(job: Job) {
        CoroutineScope(IO + job).launch {
            delay(5000)
            if (HomeFragment.driving) {
                createJob()
                withContext(Main) {
//                    requestTrackDriver()
                }
            } else {
//                locationManager.stopLocationTracking()
                job.cancel()
            }
        }
    }
    //---------------------------------------------------------------------------------------------- counter


    //---------------------------------------------------------------------------------------------- onError
    override fun onError(errorType: EnumErrorType, message: String) {
        Log.d("meri", message)
    }
    //---------------------------------------------------------------------------------------------- onError


    //---------------------------------------------------------------------------------------------- requestTrackDriver
    private fun requestTrackDriver() {
        location?.let { loc ->
            val model = TrackDriverRequestModel(1, loc.latitude.toFloat(), loc.longitude.toFloat())
            trackingDriverViewModel.requestTrackDriver(model)
                .observe(this@TrackingService) { response ->
                    response?.let {
                        onError(EnumErrorType.UNKNOWN, it.message)
                    }
                }
        }
    }
    //---------------------------------------------------------------------------------------------- requestTrackDriver


    override fun onReceiveSignalR(message: String) {
        TODO("Not yet implemented")
    }

}