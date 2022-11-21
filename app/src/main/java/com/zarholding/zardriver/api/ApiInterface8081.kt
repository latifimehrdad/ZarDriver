package com.zarholding.zardriver.api

import com.zarholding.zardriver.model.request.LoginRequestModel
import com.zarholding.zardriver.model.response.LoginResponseModel
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by m-latifi on 11/8/2022.
 */

interface ApiInterface8081 {

    companion object {
        const val api = "/Api"
        const val v1 = "$api/V1"
        const val account = "$v1/Account"

    }

    @POST("$account/login")
    suspend fun requestLogin(
        @Body login: LoginRequestModel
    ): LoginResponseModel

}