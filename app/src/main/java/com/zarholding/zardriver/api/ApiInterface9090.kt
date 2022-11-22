package com.zarholding.zardriver.api

import com.zarholding.zardriver.model.request.LoginRequestModel
import com.zarholding.zardriver.model.response.LoginResponseModel
import com.zarholding.zardriver.model.response.TripResponseModel
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * Created by m-latifi on 11/8/2022.
 */

interface ApiInterface9090 {

    companion object {
        const val api = "/Api"
        const val v1 = "$api/V1"
        const val driver = "$v1/Driver"

    }

    @POST("$driver/login-driver")
    suspend fun requestLogin(
        @Body login: LoginRequestModel
    ): LoginResponseModel


    @GET("$driver/start-trip-driver")
    suspend fun requestStartTripDriver(
        @Header("Authorization") token : String
    ) : TripResponseModel

}