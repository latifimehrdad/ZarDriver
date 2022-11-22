package com.zarholding.zardriver.hilt

import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zardriver.api.ApiInterface9090
import com.zarholding.zardriver.view.activity.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
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
    @Named("Normal")
    fun provideBaseUrl(): String {
        return "http://192.168.50.153:9090"
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
    fun provideApiService(@Named("Normal") retrofit: Retrofit): ApiInterface9090 {
        return retrofit.create(ApiInterface9090::class.java)
    }
    //---------------------------------------------------------------------------------------------- provideApiService


}