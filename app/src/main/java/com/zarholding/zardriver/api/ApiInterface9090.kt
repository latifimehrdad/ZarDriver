package com.zarholding.zardriver.api

import com.zarholding.zardriver.model.request.TrackDriverRequestModel
import com.zarholding.zardriver.model.response.TrackingResponseModel
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by m-latifi on 11/8/2022.
 */

interface ApiInterface9090 {

    companion object {
        const val api = "/Api"
        const val v1 = "$api/V1"
        const val account = "$v1/Account"
        const val tracking = "$v1/DriverTracking"

    }


    @POST("$tracking/track-driver")
    suspend fun requestTrackDriver(
        @Body track: TrackDriverRequestModel
    ): TrackingResponseModel


}