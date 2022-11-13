package com.zarholding.zardriver.repository

import com.zar.core.tools.api.apiCall
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zardriver.api.ApiInterface8081
import com.zarholding.zardriver.model.request.LoginRequestModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/9/2022.
 */

class LoginRepository @Inject constructor(private val apiInterface8081: ApiInterface8081) {

    @Inject lateinit var emitter: RemoteErrorEmitter

    //---------------------------------------------------------------------------------------------- requestLogin
    fun requestLogin(login : LoginRequestModel) = apiCall(emitter){apiInterface8081.requestLogin(login)}
    //---------------------------------------------------------------------------------------------- requestLogin

}