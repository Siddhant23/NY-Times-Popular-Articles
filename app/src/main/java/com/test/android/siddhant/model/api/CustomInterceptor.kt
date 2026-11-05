package com.test.android.siddhant.model.api

import android.content.Context
import com.test.android.siddhant.BuildConfig
import com.test.android.siddhant.R
import com.test.android.siddhant.utils.ApiException
import com.test.android.siddhant.utils.AppConstant
import com.test.android.siddhant.utils.NoConnectivityException
import com.test.android.siddhant.utils.Util
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Adds the NYT `api-key` query parameter and turns failures into typed, user-presentable errors:
 * - no network, DNS failure, connection refused, or timeout → [NoConnectivityException]
 * - non-2xx response → [ApiException] carrying the HTTP code (the response body is closed first)
 */
@Singleton
class CustomInterceptor
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            if (!Util.isNetworkAvailable(context)) {
                throw NoConnectivityException(context.getString(R.string.error_internet))
            }

            val request = chain.request()
            val url =
                request.url
                    .newBuilder()
                    .addQueryParameter(AppConstant.KEY_API, BuildConfig.API_KEY)
                    .build()

            val response =
                try {
                    chain.proceed(request.newBuilder().url(url).build())
                } catch (e: UnknownHostException) {
                    throw NoConnectivityException(context.getString(R.string.error_internet), e)
                } catch (e: ConnectException) {
                    throw NoConnectivityException(context.getString(R.string.error_internet), e)
                } catch (e: InterruptedIOException) {
                    // Covers SocketTimeoutException and OkHttp call timeouts ("timeout").
                    throw NoConnectivityException(context.getString(R.string.error_internet), e)
                }

            if (response.isSuccessful) return response

            val code = response.code
            // Release the connection: an unclosed error body leaks it back to the pool.
            response.close()
            throw ApiException(code, messageFor(code))
        }

        private fun messageFor(code: Int): String =
            when (code) {
                401, 403 -> context.getString(R.string.error_unauthorized)
                429 -> context.getString(R.string.error_rate_limited)
                in 500..599 -> context.getString(R.string.error_server)
                else -> context.getString(R.string.error_http, code)
            }
    }
