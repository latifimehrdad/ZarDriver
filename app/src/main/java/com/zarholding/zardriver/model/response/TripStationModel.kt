package com.zarholding.zardriver.model.response

/**
 * Created by m-latifi on 11/22/2022.
 */

data class TripStationModel(
    val Id : Int,
    val CommuteTripsId : Int,
    val CommuteTripName : String?,
    val DestinationName : String?,
    val OriginName : String?,
    val StationName : String?,
    val ArriveTime : String?,
    val StationLat : Float,
    val SationLong : Float,
    val StationNum : Float,
    val IsActive : Boolean
)
