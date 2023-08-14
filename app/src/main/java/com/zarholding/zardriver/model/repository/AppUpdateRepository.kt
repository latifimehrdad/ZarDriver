package com.zarholding.zardriver.model.repository

import com.zar.core.tools.api.apiCall
import javax.inject.Inject


/**
 * create by m-latifi on 4/25/2023
 */

class AppUpdateRepository @Inject constructor() : ZarRepository() {

    //---------------------------------------------------------------------------------------------- requestGetAppVersion
    suspend fun requestGetAppVersion(appName: String) =
        apiCall { api.requestGetAppVersion(appName) }
    //---------------------------------------------------------------------------------------------- requestGetAppVersion

}