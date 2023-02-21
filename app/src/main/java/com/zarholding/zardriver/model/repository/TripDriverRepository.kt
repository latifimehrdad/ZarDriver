package com.zarholding.zardriver.model.repository

import com.zar.core.tools.api.apiCall
import com.zarholding.zardriver.model.api.ApiInterface9090
import javax.inject.Inject

/**
 * Created by m-latifi on 11/22/2022.
 */

class TripDriverRepository @Inject constructor(
    private val apiInterface9090: ApiInterface9090,
    private val tokenRepository: TokenRepository
    ) {


    //---------------------------------------------------------------------------------------------- requestStartTripDriver
    suspend fun requestStartTripDriver() =
        apiCall { apiInterface9090.requestStartTripDriver(tokenRepository.getBearerToken()) }
    //---------------------------------------------------------------------------------------------- requestStartTripDriver
}