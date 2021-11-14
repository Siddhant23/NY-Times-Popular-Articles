package com.test.android.siddhant.di

import com.test.android.siddhant.model.api.ApiService
import com.test.android.siddhant.utils.AppConstant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =  Retrofit.Builder()
            .baseUrl(AppConstant.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    fun providesOkHttpClient(): OkHttpClient {
        return OkHttpClient().newBuilder()
            .connectTimeout(15, TimeUnit.SECONDS)   // connect timeout
            .readTimeout(15, TimeUnit.SECONDS)      // socket timeout
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    fun providesAPIService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

}