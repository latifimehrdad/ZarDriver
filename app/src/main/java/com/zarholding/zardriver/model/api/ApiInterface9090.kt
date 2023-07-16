package com.zarholding.zardriver.model.api

import com.zarholding.zardriver.model.data.request.LoginRequestModel
import com.zarholding.zardriver.model.data.response.GeneralResponse
import com.zarholding.zardriver.model.data.response.TripModel
import com.zarholding.zardriver.model.data.response.DriverModel
import retrofit2.Response
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
    ): Response<GeneralResponse<String?>>


    @GET("$driver/start-trip-driver")
    suspend fun requestStartTripDriver(
        @Header("Authorization") token : String
    ) : Response<GeneralResponse<TripModel?>>

    @GET("$driver/get-driver-info")
    suspend fun requestGetDriverInfo(
        @Header("Authorization") token : String
    ): Response<GeneralResponse<DriverModel?>>

}