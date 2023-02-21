package com.zarholding.zardriver.model.repository

import android.content.SharedPreferences
import com.zarholding.zardriver.tools.CompanionValues
import javax.inject.Inject

/**
 * Created by m-latifi on 11/22/2022.
 */

class TokenRepository @Inject constructor(private val sp: SharedPreferences ) {

    //---------------------------------------------------------------------------------------------- userIsEntered
    fun userIsEntered(): Boolean {
        val token = sp.getString(CompanionValues.TOKEN, null)
        return token != null
    }
    //---------------------------------------------------------------------------------------------- userIsEntered


    //---------------------------------------------------------------------------------------------- getBearerToken
    fun getBearerToken() = "Bearer ${sp.getString(CompanionValues.TOKEN, null)}"
    //---------------------------------------------------------------------------------------------- getBearerToken

    //---------------------------------------------------------------------------------------------- getToken
    fun getToken() = sp.getString(CompanionValues.TOKEN, null)
    //---------------------------------------------------------------------------------------------- getToken

}