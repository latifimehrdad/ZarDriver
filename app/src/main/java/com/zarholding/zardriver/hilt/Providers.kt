package com.zarholding.zardriver.hilt

import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zardriver.api.ApiInterface
import com.zarholding.zardriver.view.activity.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Created by m-latifi on 11/8/2022.
 */

@Module
@InstallIn(SingletonComponent::class)
class Providers {


    //---------------------------------------------------------------------------------------------- provideBaseUrl
    @Provides
    @Singleton
    fun provideBaseUrl(): String {
        return "http://192.168.50.153:8081"
    }
    //---------------------------------------------------------------------------------------------- provideBaseUrl


    //---------------------------------------------------------------------------------------------- provideRemoteErrorEmitter
    @Provides
    @Singleton
    fun provideRemoteErrorEmitter() : RemoteErrorEmitter {
        return MainActivity()
    }
    //---------------------------------------------------------------------------------------------- provideRemoteErrorEmitter



    //---------------------------------------------------------------------------------------------- provideApiService
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }
    //---------------------------------------------------------------------------------------------- provideApiService


}