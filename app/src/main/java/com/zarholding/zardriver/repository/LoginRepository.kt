package com.zarholding.zardriver.repository

import com.zar.core.tools.api.apiCall
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zardriver.api.ApiInterface9090
import com.zarholding.zardriver.model.request.LoginRequestModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/9/2022.
 */

class LoginRepository @Inject constructor(private val apiInterface9090: ApiInterface9090) {

    @Inject
    lateinit var emitter: RemoteErrorEmitter

    //---------------------------------------------------------------------------------------------- requestLogin
    fun requestLogin(login: LoginRequestModel) = apiCall(emitter)
    { apiInterface9090.requestLogin(login) }
    //---------------------------------------------------------------------------------------------- requestLogin

}