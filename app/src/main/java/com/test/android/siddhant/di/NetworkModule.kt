package com.test.android.siddhant.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.test.android.siddhant.BuildConfig
import com.test.android.siddhant.model.api.ApiService
import com.test.android.siddhant.utils.AppConstant
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providesGsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

    @Singleton
    @Provides
    fun providesRetrofit(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(gsonConverterFactory)
        .build()

    @Singleton
    @Provides
    fun providesOkHttpClient(
        chuckerInterceptor: ChuckerInterceptor,
        interceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient().newBuilder()
            .addInterceptor(chuckerInterceptor)
            .addInterceptor(interceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .callTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Singleton
    @Provides
    fun providesChuckerInterceptor(@ApplicationContext context: Context): ChuckerInterceptor {
        return ChuckerInterceptor
            .Builder(context)
            .collector(ChuckerCollector(context))
            .alwaysReadResponseBody(true)
            .build()
    }

    @Singleton
    @Provides
    fun providesInterceptor() = Interceptor { chain ->
        val request = chain.request()
        val url = request.url()
        val queryParams = url
            .newBuilder()
            .addQueryParameter(AppConstant.KEY_API, BuildConfig.API_KEY)
            .build()
        val requestBuilder = request
            .newBuilder()
            .url(queryParams)
            .build()
        return@Interceptor chain.proceed(requestBuilder)
    }

    @Singleton
    @Provides
    fun providesAPIService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

}