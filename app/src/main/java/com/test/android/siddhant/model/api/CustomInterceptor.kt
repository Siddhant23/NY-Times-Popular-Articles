package com.test.android.siddhant.model.api

import com.test.android.siddhant.utils.AppConstant
import com.test.android.siddhant.utils.HttpStatusException
import com.test.android.siddhant.utils.InvalidApiKeyException
import com.test.android.siddhant.utils.NoConnectivityException
import com.test.android.siddhant.utils.RateLimitedException
import com.test.android.siddhant.utils.ServerException
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Adds the NYT `api-key` query parameter to every request and turns non-2xx responses
 * into typed [com.test.android.siddhant.utils.ApiException]s.
 *
 * @param isOnline connectivity pre-check, so the user gets a clear "offline" error instead of a DNS timeout.
 * @param apiKey the NYT API key (`BuildConfig.API_KEY` in production).
 */
class CustomInterceptor(
    private val isOnline: () -> Boolean,
    private val apiKey: String,
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        if (!isOnline()) throw NoConnectivityException()

        val request = chain.request()
        val url =
            request.url
                .newBuilder()
                .addQueryParameter(AppConstant.KEY_API, apiKey)
                .build()
        val response = chain.proceed(request.newBuilder().url(url).build())
        if (response.isSuccessful) return response

        // Release the connection before failing; the body is never read on errors.
        val code = response.code
        response.close()
        throw when (code) {
            401, 403 -> InvalidApiKeyException(code)
            429 -> RateLimitedException()
            in 500..599 -> ServerException(code)
            else -> HttpStatusException(code)
        }
    }
}
