package com.zarholding.zardriver.hilt

import com.google.gson.Gson
import com.zar.core.tools.api.interfaces.RemoteErrorEmitter
import com.zarholding.zardriver.api.ApiInterface8081
import com.zarholding.zardriver.view.activity.MainActivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
    fun provideApiService(@Named("Normal") retrofit: Retrofit): ApiInterface8081 {
        return retrofit.create(ApiInterface8081::class.java)
    }
    //---------------------------------------------------------------------------------------------- provideApiService


}