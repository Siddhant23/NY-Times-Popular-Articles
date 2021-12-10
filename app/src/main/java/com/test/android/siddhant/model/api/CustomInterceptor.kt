package com.test.android.siddhant.model.api

import android.content.Context
import com.test.android.siddhant.BuildConfig
import com.test.android.siddhant.R
import com.test.android.siddhant.utils.AppConstant
import com.test.android.siddhant.utils.NoConnectivityException
import com.test.android.siddhant.utils.UnknownException
import com.test.android.siddhant.utils.Util
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomInterceptor @Inject constructor(@ApplicationContext private val context: Context) :
    Interceptor {

    @Throws(Exception::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!Util.isNetworkAvailable(context))
            throw NoConnectivityException(context.getString(R.string.error_internet))

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
        val response = chain.proceed(requestBuilder)
        return if (response.isSuccessful) response else throw UnknownException(response.message())
    }
}