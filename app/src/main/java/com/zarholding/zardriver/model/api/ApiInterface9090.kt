package com.zarholding.zardriver.model.api

import com.zarholding.zardriver.model.data.enums.EnumEntityType
import com.zarholding.zardriver.model.data.request.LoginRequestModel
import com.zarholding.zardriver.model.data.response.AppVersionModel
import com.zarholding.zardriver.model.data.response.GeneralResponse
import com.zarholding.zardriver.model.data.response.TripModel
import com.zarholding.zardriver.model.data.response.DriverModel
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.Streaming

/**
 * Created by m-latifi on 11/8/2022.
 */

interface ApiInterface9090 {

    companion object {
        const val api = "/Api"
        const val v1 = "$api/V1"
        const val driver = "$v1/Driver"
        const val files = "$v1/Files"

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


    //---------------------------------------------------------------------------------------------- requestGetAppVersion
    @GET("$files/files-get-appVersion")
    suspend fun requestGetAppVersion(
        @Query("app") app: String
    ): Response<GeneralResponse<AppVersionModel?>>

    @Streaming
    @GET("$files/files-get-apk")
    suspend fun downloadApkFile(
        @Query("SystemType") systemType: String,
        @Query("EntityType") entityType: EnumEntityType,
        @Query("FileName") fileName: String
    ): Response<ResponseBody>
    //---------------------------------------------------------------------------------------------- requestGetAppVersion


}