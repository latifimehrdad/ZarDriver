package com.zarholding.zardriver.model.response

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by m-latifi on 11/22/2022.
 */

data class TripModel(
    val id : Int,
    val commuteTripName : String?,
    val originName : String?,
    val destinationName : String?,
    val leaveTime : String?,
    val commuteDriverId : Int,
    val commuteDriverName : String?,
    val companyCode : String?,
    val companyName : String?,
    val tripPoints : List<TripPointModel>?,
    val stations : List<TripStationModel>?,
    val strTripPoint : String?
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(TripPointModel),
        parcel.createTypedArrayList(TripStationModel),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(commuteTripName)
        parcel.writeString(originName)
        parcel.writeString(destinationName)
        parcel.writeString(leaveTime)
        parcel.writeInt(commuteDriverId)
        parcel.writeString(commuteDriverName)
        parcel.writeString(companyCode)
        parcel.writeString(companyName)
        parcel.writeTypedList(tripPoints)
        parcel.writeTypedList(stations)
        parcel.writeString(strTripPoint)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TripModel> {
        override fun createFromParcel(parcel: Parcel): TripModel {
            return TripModel(parcel)
        }

        override fun newArray(size: Int): Array<TripModel?> {
            return arrayOfNulls(size)
        }
    }

}
