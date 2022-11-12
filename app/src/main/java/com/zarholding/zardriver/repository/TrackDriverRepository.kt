package com.zarholding.zardriver.repository

import com.zar.core.tools.api.apiCall
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zardriver.api.ApiInterface
import com.zarholding.zardriver.model.request.TrackDriverRequestModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/12/2022.
 */

class TrackDriverRepository @Inject constructor(private val apiInterface: ApiInterface){

    @Inject lateinit var emitter: RemoteErrorEmitter

    //---------------------------------------------------------------------------------------------- requestTrackDriver
    fun requestTrackDriver(trackDriver : TrackDriverRequestModel) =
        apiCall(emitter){apiInterface.requestTrackDriver("http://192.168.50.153:9090", trackDriver)}
    //---------------------------------------------------------------------------------------------- requestTrackDriver


}