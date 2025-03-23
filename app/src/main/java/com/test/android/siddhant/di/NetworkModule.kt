package com.test.android.siddhant.di

import android.content.Context
import com.test.android.siddhant.BuildConfig
import com.test.android.siddhant.model.api.ApiService
import com.test.android.siddhant.model.api.CustomInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
        gsonConverterFactory: GsonConverterFactory,
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()

    @Singleton
    @Provides
    fun providesOkHttpClient(customInterceptor: CustomInterceptor): OkHttpClient {
        return OkHttpClient().newBuilder()
            .addInterceptor(customInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .callTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Singleton
    @Provides
    fun providesInterceptor(
        @ApplicationContext context: Context,
    ) = CustomInterceptor(context)

    @Singleton
    @Provides
    fun providesAPIService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
}
