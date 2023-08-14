package com.zarholding.zardriver.model.repository

import com.zarholding.zardriver.model.api.ApiInterface9090
import javax.inject.Inject

/**
 * Created by m-latifi on 8/8/2023.
 */

open class ZarRepository @Inject constructor() {

    @Inject
    lateinit var api: ApiInterface9090

    @Inject
    lateinit var tokenRepository: TokenRepository

}