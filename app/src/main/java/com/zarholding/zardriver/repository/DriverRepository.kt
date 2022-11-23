package com.zarholding.zardriver.repository

import com.zar.core.tools.api.apiCall
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zardriver.api.ApiInterface9090
import javax.inject.Inject

/**
 * Created by m-latifi on 11/23/2022.
 */

class DriverRepository @Inject constructor(private val apiInterface9090: ApiInterface9090) {

    @Inject
    lateinit var emitter: RemoteErrorEmitter


    //---------------------------------------------------------------------------------------------- requestGetDriverInfo
    fun requestGetDriverInfo(token: String) =
        apiCall(emitter) { apiInterface9090.requestGetDriverInfo(token) }
    //---------------------------------------------------------------------------------------------- requestGetDriverInfo

}