package com.zarholding.zardriver.model.repository

import com.zar.core.tools.hilt.ProgressResponseBody
import com.zarholding.zardriver.di.Providers
import com.zarholding.zardriver.model.api.ApiInterface9090
import com.zarholding.zardriver.model.data.enums.EnumEntityType
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Inject


/**
 * create by m-latifi on 4/25/2023
 */

class DownloadFileRepository @Inject constructor() : ZarRepository() {

    //---------------------------------------------------------------------------------------------- downloadApkFile
    suspend fun downloadApkFile(systemType: String, fileName: String) =
        retrofit()
            .create(ApiInterface9090::class.java)
            .downloadApkFile(systemType, EnumEntityType.APK, fileName)
    //---------------------------------------------------------------------------------------------- downloadApkFile


    //---------------------------------------------------------------------------------------------- retrofit
    private fun retrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(Providers.url)
        .client(httpClient())
        .build()
    //---------------------------------------------------------------------------------------------- retrofit


    //---------------------------------------------------------------------------------------------- httpClient
    private fun httpClient() = OkHttpClient()
        .newBuilder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(interceptor())
        .build()
    //---------------------------------------------------------------------------------------------- httpClient


    //---------------------------------------------------------------------------------------------- interceptor
    private fun interceptor() = Interceptor { chain ->
        val originalResponse = chain.proceed(chain.request())
        originalResponse.newBuilder()
            .body(ProgressResponseBody(originalResponse.body!!) { _, _, _ -> })
            .build()
    }
    //---------------------------------------------------------------------------------------------- interceptor

}