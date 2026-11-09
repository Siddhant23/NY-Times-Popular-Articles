package com.test.android.siddhant.di

import android.content.Context
import com.test.android.siddhant.BuildConfig
import com.test.android.siddhant.model.api.ApiService
import com.test.android.siddhant.model.api.CustomInterceptor
import com.test.android.siddhant.utils.AppConstant
import com.test.android.siddhant.utils.Util
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providesJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Singleton
    @Provides
    fun providesRetrofit(
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(
        customInterceptor: CustomInterceptor,
    ): OkHttpClient {
        val builder = OkHttpClient().newBuilder()
            .addInterceptor(customInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .callTimeout(10, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(debugLoggingInterceptor())
        }

        return builder.build()
    }

    /** Logs full requests/responses in debug builds, with the API key redacted from URLs. */
    internal fun debugLoggingInterceptor(
        logger: HttpLoggingInterceptor.Logger = HttpLoggingInterceptor.Logger.DEFAULT,
    ): HttpLoggingInterceptor =
        HttpLoggingInterceptor(logger).apply {
            level = HttpLoggingInterceptor.Level.BODY
            redactQueryParams(AppConstant.KEY_API)
        }

    @Singleton
    @Provides
    fun providesInterceptor(
        @ApplicationContext context: Context,
    ) = CustomInterceptor(
        isOnline = { Util.isNetworkAvailable(context) },
        apiKey = BuildConfig.API_KEY,
    )

    @Singleton
    @Provides
    fun providesAPIService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
}
