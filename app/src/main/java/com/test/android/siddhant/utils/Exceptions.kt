package com.test.android.siddhant.utils

import java.io.IOException

/** The device has no usable network, or the API host could not be reached. */
class NoConnectivityException(
    message: String,
    cause: Throwable? = null,
) : IOException(message, cause)

/**
 * The NYT API answered with a non-2xx status.
 *
 * Extends [IOException] so OkHttp/Retrofit propagate it to the caller unchanged.
 */
class ApiException(
    val code: Int,
    message: String,
) : IOException(message)
