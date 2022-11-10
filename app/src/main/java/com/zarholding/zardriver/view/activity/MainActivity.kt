package com.zarholding.zardriver.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.zar.core.enums.EnumAuthorizationType
import com.zar.core.enums.EnumErrorType
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zardriver.R
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by m-latifi on 11/8/2022.
 */

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), RemoteErrorEmitter {

    //---------------------------------------------------------------------------------------------- companion object
    companion object {
        lateinit var remoteErrorEmitter: RemoteErrorEmitter
    }
    //---------------------------------------------------------------------------------------------- companion object


    //---------------------------------------------------------------------------------------------- onCreate
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
    //---------------------------------------------------------------------------------------------- onCreate


    //---------------------------------------------------------------------------------------------- onError
    override fun onError(errorType: EnumErrorType, message: String) {
        remoteErrorEmitter.onError(errorType, message)
    }
    //---------------------------------------------------------------------------------------------- onError


    //---------------------------------------------------------------------------------------------- unAuthorization
    override fun unAuthorization(type: EnumAuthorizationType, message: String) {
        remoteErrorEmitter.unAuthorization(type, message)
    }
    //---------------------------------------------------------------------------------------------- unAuthorization

}