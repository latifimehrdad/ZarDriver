package com.zarholding.zardriver.di

import com.zarholding.zardriver.model.api.ApiInterface9090
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

    companion object {
        //        const val url = "http://5.160.125.98:5081"
//        const val url = "http://192.168.50.153:9090"//supperApp
        const val url = "https://app.zarholding.com:5081"//supperApp
    }


    //---------------------------------------------------------------------------------------------- provideBaseUrl
    @Provides
    @Singleton
    fun provideBaseUrl(): String {
        return url
    }
    //---------------------------------------------------------------------------------------------- provideBaseUrl


    //---------------------------------------------------------------------------------------------- provideApiService
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiInterface9090 {
        return retrofit.create(ApiInterface9090::class.java)
    }
    //---------------------------------------------------------------------------------------------- provideApiService


}