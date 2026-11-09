package com.test.android.siddhant.utils

import androidx.annotation.StringRes
import com.test.android.siddhant.R
import java.io.IOException

/**
 * Failures raised by [com.test.android.siddhant.model.api.CustomInterceptor].
 * They extend [IOException] because OkHttp interceptors may only throw IOExceptions.
 */
sealed class ApiException(message: String) : IOException(message)

class NoConnectivityException : ApiException("No network connection")

/** HTTP 401/403: the NYT API key is missing, invalid, or not enabled for this API. */
class InvalidApiKeyException(val code: Int) : ApiException("HTTP $code: invalid or missing API key")

/** HTTP 429: the NYT per-minute or per-day quota was exceeded. */
class RateLimitedException : ApiException("HTTP 429: rate limited")

/** HTTP 5xx. */
class ServerException(val code: Int) : ApiException("HTTP $code: server error")

/** Any other non-2xx status. */
class HttpStatusException(val code: Int) : ApiException("HTTP $code")

/** Maps a failure to the user-facing message shown on the error screen. */
@StringRes
fun Throwable.toMessageRes(): Int =
    when (this) {
        is InvalidApiKeyException -> R.string.error_api_key
        is RateLimitedException -> R.string.error_rate_limited
        is ServerException -> R.string.error_server
        is HttpStatusException -> R.string.error_unknown
        // NoConnectivityException and other transport failures (DNS, timeouts, resets).
        is IOException -> R.string.error_internet
        else -> R.string.error_unknown
    }
