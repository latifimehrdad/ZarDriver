package com.zarholding.zardriver.repository

import com.zar.core.tools.api.apiCall
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zardriver.api.ApiInterface9090
import com.zarholding.zardriver.model.request.TrackDriverRequestModel
import javax.inject.Inject

/**
 * Created by m-latifi on 11/12/2022.
 */

class TrackDriverRepository @Inject constructor(private val apiInterface9090: ApiInterface9090){

    @Inject lateinit var emitter: RemoteErrorEmitter

    //---------------------------------------------------------------------------------------------- requestTrackDriver
    fun requestTrackDriver(trackDriver : TrackDriverRequestModel) =
        apiCall(emitter){apiInterface9090.requestTrackDriver(trackDriver)}
    //---------------------------------------------------------------------------------------------- requestTrackDriver


}