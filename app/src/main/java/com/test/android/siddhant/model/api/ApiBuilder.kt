package com.test.android.siddhant.model.api

import com.test.android.siddhant.utils.AppConstant
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiBuilder {

    private val retrofitBuilder: Retrofit.Builder by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstant.BASE_URL)
            .client(okHttpBuilder)
            .addConverterFactory(GsonConverterFactory.create())
    }

    private val okHttpBuilder: OkHttpClient by lazy {
        OkHttpClient().newBuilder()
            .connectTimeout(30, TimeUnit.SECONDS)   // connect timeout
            .readTimeout(30, TimeUnit.SECONDS)      // socket timeout
            .retryOnConnectionFailure(true)
            .build()
    }


    internal val apiService: ApiService by lazy {
        retrofitBuilder
            .build()
            .create(ApiService::class.java)
    }

}