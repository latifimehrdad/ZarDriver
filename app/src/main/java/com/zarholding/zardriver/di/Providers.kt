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


    //---------------------------------------------------------------------------------------------- provideBaseUrl
    @Provides
    @Singleton
    fun provideBaseUrl(): String {
        return "http://192.168.50.153:9090"
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