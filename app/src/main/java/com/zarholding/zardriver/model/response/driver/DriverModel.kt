package com.zarholding.zardriver.model.response.driver

import android.os.Parcel
import android.os.Parcelable

/**
 * Created by m-latifi on 11/23/2022.
 */

data class DriverModel(
    val id : Int,
    val fullName : String?,
    val mobile : String?,
    val nationalCode : String?,
    val pelak : String?,
    val description : String?,
    val carImage : String?,
    val carImageName : String?,
    val driverImage : String?,
    val driverImageName : String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(fullName)
        parcel.writeString(mobile)
        parcel.writeString(nationalCode)
        parcel.writeString(pelak)
        parcel.writeString(description)
        parcel.writeString(carImage)
        parcel.writeString(carImageName)
        parcel.writeString(driverImage)
        parcel.writeString(driverImageName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DriverModel> {
        override fun createFromParcel(parcel: Parcel): DriverModel {
            return DriverModel(parcel)
        }

        override fun newArray(size: Int): Array<DriverModel?> {
            return arrayOfNulls(size)
        }
    }
}
