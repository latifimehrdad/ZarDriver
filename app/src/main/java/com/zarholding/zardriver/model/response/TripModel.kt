package com.zarholding.zardriver.model.response

/**
 * Created by m-latifi on 11/22/2022.
 */

data class TripModel(
    val Id : Int,
    val CommuteTripName : String?,
    val OriginName : String?,
    val DestinationName : String?,
    val LeaveTime : String?,
    val CommuteDriverId : Int,
    val CommuteDriverName : String?,
    val CompanyCode : String?,
    val CompanyName : String?,
    val tripPoints : List<TripPointModel>?,
    val stations : List<TripStationModel>?,
    val strTripPoint : String?
)
