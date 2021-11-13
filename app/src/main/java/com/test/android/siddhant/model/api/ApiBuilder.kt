package com.test.android.siddhant.model.api

import com.test.android.siddhant.utils.AppConstant
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiBuilder {

     val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstant.BASE_URL)
            .client(okHttpBuilder)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private val okHttpBuilder: OkHttpClient by lazy {
        OkHttpClient().newBuilder()
            .connectTimeout(15, TimeUnit.SECONDS)   // connect timeout
            .readTimeout(15, TimeUnit.SECONDS)      // socket timeout
            .retryOnConnectionFailure(true)
            .build()
    }
}