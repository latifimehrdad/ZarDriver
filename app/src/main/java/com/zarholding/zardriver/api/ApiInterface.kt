package com.zarholding.zardriver.api

import com.zarholding.zardriver.model.request.LoginRequestModel
import com.zarholding.zardriver.model.request.TrackDriverRequestModel
import com.zarholding.zardriver.model.response.BaseResponseAbstractModel
import com.zarholding.zardriver.model.response.LoginResponseModel
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.POST
import retrofit2.http.Url

/**
 * Created by m-latifi on 11/8/2022.
 */

interface ApiInterface {

    companion object {
        const val api = "/Api"
        const val v1 = "$api/V1"
        const val account = "$v1/Account"
        const val tracking = "$v1/DriverTracking"

    }

    @POST("$account/login")
    suspend fun requestLogin(
        @Body login: LoginRequestModel
    ): LoginResponseModel


    @POST("$tracking/track-driver")
    suspend fun requestTrackDriver(
        @Url url: String,
        @Body track: TrackDriverRequestModel
    ): BaseResponseAbstractModel


}