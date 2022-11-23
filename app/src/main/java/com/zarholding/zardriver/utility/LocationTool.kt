package com.zarholding.zardriver.utility

import android.location.Location
import android.location.LocationManager
import android.util.Log
import com.zarholding.zardriver.model.response.TripStationModel

/**
 * Created by m-latifi on 11/23/2022.
 */

class LocationTool {

    private val tolerance = 200.0

    //---------------------------------------------------------------------------------------------- checkLocationNearbyStation
    fun checkLocationNearbyStation(
        stations: List<TripStationModel>?,
        currentLocation: Location
    ): Int {
        var stationId = -1
        stations?.let { it ->
            for (i in it.indices)
                if (!it[i].isNotification) {
                    val station = Location(LocationManager.GPS_PROVIDER)
                    station.latitude = it[i].stationLat.toDouble()
                    station.longitude = it[i].sationLong.toDouble()
                    val distance = measureDistance(currentLocation, station)
                    if (distance < tolerance) {
                        Log.i("meri", "distance = $distance name = ${it[i].stationName}")
                        it[i].isNotification = true
                        if (i + 1 < it.size)
                            stationId = i + 1
                        break
                    }
                }
        }
        return stationId
    }
    //---------------------------------------------------------------------------------------------- checkLocationNearbyStation


    //---------------------------------------------------------------------------------------------- measureDistance
    private fun measureDistance(Old: Location, New: Location): Float {
        val results = FloatArray(1)
        Location.distanceBetween(
            Old.latitude, Old.longitude,
            New.latitude, New.longitude, results
        )
        return if (results.isNotEmpty()) results[0] else 0f
    }
    //---------------------------------------------------------------------------------------------- measureDistance

}