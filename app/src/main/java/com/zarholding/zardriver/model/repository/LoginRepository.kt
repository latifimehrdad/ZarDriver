package com.zarholding.zardriver.model.repository

import com.zar.core.tools.api.apiCall
import com.zarholding.zardriver.model.api.ApiInterface9090
import com.zarholding.zardriver.model.request.LoginRequestModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/9/2022.
 */

class LoginRepository @Inject constructor(private val apiInterface9090: ApiInterface9090) {


    //---------------------------------------------------------------------------------------------- requestLogin
    suspend fun requestLogin(login: LoginRequestModel) =
        apiCall { apiInterface9090.requestLogin(login) }
    //---------------------------------------------------------------------------------------------- requestLogin

}