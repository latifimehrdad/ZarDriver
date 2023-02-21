package com.zarholding.zardriver.model.repository

import com.zar.core.tools.api.apiCall
import com.zarholding.zardriver.api.ApiInterface9090
import javax.inject.Inject

/**
 * Created by m-latifi on 11/23/2022.
 */

class DriverRepository @Inject constructor(
    private val apiInterface9090: ApiInterface9090,
    private val tokenRepository: TokenRepository
) {


    //---------------------------------------------------------------------------------------------- requestGetDriverInfo
    suspend fun requestGetDriverInfo() =
        apiCall { apiInterface9090.requestGetDriverInfo(tokenRepository.getBearerToken()) }
    //---------------------------------------------------------------------------------------------- requestGetDriverInfo

}